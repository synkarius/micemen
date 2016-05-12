package data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import file.Board;
import model.CheeseException;
import model.CheeseGrid;

public class DataAccessor {
    
    private static final String TABLE     = "calcdboards";
    private static final String COL_ID    = "id";
    private static final String COL_BOARD = "board";
    private static final String COL_VALUE = "value";
    
    private Connection          connection;
    
    public DataAccessor() {
        
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mouse.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
    
    public void create() {
        try {
            String sql = "create table if not exists " + TABLE + //
                    " (" + COL_ID + " int primary key not null, "//
                    + COL_BOARD + " text not null, "//
                    + COL_VALUE + " int not null) ";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new CheeseException(e);
        }
    }
    
    public Integer valueOf(CheeseGrid grid) {
        return valueOf(Board.getBoardString(grid));
    }
    
    public Integer valueOf(String board) {
        Integer value = null;
        try {
            PreparedStatement stmt = connection
                    .prepareStatement("select * from " + TABLE + " where " + COL_BOARD + " = '" + board + "'");
            ResultSet rez = stmt.executeQuery();
            if (rez.next()) {
                value = rez.getInt(COL_VALUE);
            }
            stmt.close();
        } catch (SQLException e) {
            throw new CheeseException(e);
        }
        return value;
    }
    
    public void insert(String board, int value) {
        try {
            PreparedStatement stmt = connection.prepareStatement(//
                    "insert into " + TABLE + " values ("//
                            + "(select max(id) from " + TABLE + ")+1, "//
                            + "'" + board + "', "//
                            + value + ")");
            stmt.executeUpdate();
            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            throw new CheeseException(e);
        }
    }
    
    public void dump() throws IOException {
        String getAll = "select * from " + TABLE;
        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement(getAll);
            
            ResultSet data = stmt.executeQuery();
            BufferedWriter bw = null;
            while (data.next()) {
                if (bw == null)
                    bw = Files.newBufferedWriter(Paths.get("mouse_dump" + Board.timestamp() + ".txt").toAbsolutePath());
                String board = data.getString(COL_BOARD);
                int value = data.getInt(COL_VALUE);
                bw.write(board);
                bw.newLine();
                bw.write(value);
                bw.newLine();
            }
            if (bw != null)
                bw.close();
            stmt.close();
        } catch (SQLException e) {
            throw new CheeseException(e);
        }
    }
    
}
