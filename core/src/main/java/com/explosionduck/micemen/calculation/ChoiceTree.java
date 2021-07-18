package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;

public interface ChoiceTree {

    ColumnShiftCommand getBestFuture(int lookAhead);

    Command process();

    /**
     * May not need this with new impl.
     */
    void clear();

    boolean isReady();

    int getLookAhead();
}
