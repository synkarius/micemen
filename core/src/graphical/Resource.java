package graphical;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Resource {
    
    private static final Texture cheeseTex   = new Texture("cheese.png");
    private static final Texture xTex        = new Texture("x.png");
    private static final Texture redHandTex  = new Texture("redhand.png");
    private static final Texture blueHandTex = new Texture("bluehand.png");
    private static final Texture poleTex     = new Texture("pole.png");
    private static final Texture bgTex       = new Texture("bg.png");
    private static final Texture redsTex     = new Texture("redmice.png");
    private static final Texture bluesTex    = new Texture("bluemice.png");
    static {
        bgTex.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
    }
    
    public static final TextureRegion cheese   = new TextureRegion(cheeseTex, 0, 0, SceneGraph.BLOCK_SIZE,
            SceneGraph.BLOCK_SIZE);
    public static final TextureRegion redHand  = new TextureRegion(redHandTex, 0, 0, SceneGraph.BLOCK_SIZE,
            SceneGraph.BLOCK_SIZE);
    public static final TextureRegion blueHand = new TextureRegion(blueHandTex, 0, 0, SceneGraph.BLOCK_SIZE,
            SceneGraph.BLOCK_SIZE);
    public static final TextureRegion x        = new TextureRegion(xTex, 0, 0, SceneGraph.BLOCK_SIZE,
            SceneGraph.BLOCK_SIZE);
    public static final TextureRegion pole     = new TextureRegion(poleTex, 0, 0, 10, 50);
    public static final TextureRegion bg       = new TextureRegion(bgTex);
    static {
        bg.setRegion(0, 0, SceneGraph.BLOCK_SIZE * 21, SceneGraph.BLOCK_SIZE * 13);
    }
    
    public static final Sound      hi   = Gdx.audio.newSound(Gdx.files.internal("hi.wav"));
    public static final Sound      lo   = Gdx.audio.newSound(Gdx.files.internal("lo.wav"));
    public static final Sound      lo2  = Gdx.audio.newSound(Gdx.files.internal("lo2.wav"));
    public static final Sound      jump = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
    
    public static final BitmapFont large;
    public static final BitmapFont normal;
    static {
        // "Touch" : http://www.1001freefonts.com/touch.font -- free for personal use
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Touch.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 14;
        normal = generator.generateFont(parameter);
        parameter.size = 26;
        large = generator.generateFont(parameter);
    }
    
    public static enum Graphic {
        STAND(0, 0), WALK(1, 0), EAT1(2, 0), EAT2(3, 0), EAT3(0, 1), POINT(1, 1), UMBRELLA(2, 1), FACE_CAMERA(3,
                1), MUSCLE1(0, 2), MUSCLE2(1, 2), FALL(2, 2);
        
        private TextureRegion             red;
        private TextureRegion             blue;
        
        /** len: 10 */
        public static final List<Graphic> ANIM_EAT  = Arrays.asList(EAT1, EAT1, EAT1, EAT2, EAT3, STAND, EAT3, STAND,
                EAT3, STAND);
        
        /** len: 11 */
        public static final List<Graphic> ANIM_FLEX = Arrays.asList(FACE_CAMERA, FACE_CAMERA, FACE_CAMERA, FACE_CAMERA,
                MUSCLE1, MUSCLE2, MUSCLE1, MUSCLE2, MUSCLE1, MUSCLE2, FACE_CAMERA);
        
        
        
        private Graphic(int x, int y) {
            this.red = new TextureRegion(redsTex, x * SceneGraph.BLOCK_SIZE, y * SceneGraph.BLOCK_SIZE,
                    SceneGraph.BLOCK_SIZE, SceneGraph.BLOCK_SIZE);
            this.blue = new TextureRegion(bluesTex, x * SceneGraph.BLOCK_SIZE, y * SceneGraph.BLOCK_SIZE,
                    SceneGraph.BLOCK_SIZE, SceneGraph.BLOCK_SIZE);
        }
        
        public TextureRegion red() {
            return red;
        }
        
        public TextureRegion blue() {
            return blue;
        }
    }
    
}
