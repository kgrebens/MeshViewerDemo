import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * simple 3D viewer and mesh
 * Created by huson on 12/1/15.
 */
public class MeshViewerDemo  extends Application {

    public double scale = 100;

    public MeshView meshView;
    PointLight pointLight;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // setup camera:
        final PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);

        final Rotate cameraRotateX = new Rotate(0, new Point3D(1, 0, 0));
        final Rotate cameraRotateY = new Rotate(0, new Point3D(0, 1, 0));
        final Translate cameraTranslate = new Translate(0, 0, -1000);
        camera.getTransforms().addAll(cameraRotateX, cameraRotateY, cameraTranslate);

        // setup world and subscene
        final Group world = new Group();
        final SubScene subScene = new SubScene(world, 1000, 1000, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);

        // setup lights

        // color of lights
        Color farbe1 = new Color(1, 1, 1, 1);
        Color farbe2 = Color.CRIMSON;

        // PointLight
        pointLight = new PointLight(farbe2);
        pointLight.setTranslateX(100);
        pointLight.setTranslateY(100);
        pointLight.setTranslateZ(100);
        pointLight.setRotate(90);

        // AmbientLight
        AmbientLight ambient = new AmbientLight(farbe1);

        // add lights to viewer
        world.getChildren().add(ambient);
        world.getChildren().add(pointLight);

        // setup top pane and stacked pane
        final Pane topPane = new Pane();
        topPane.setPickOnBounds(false);

        final StackPane stackPane = new StackPane(subScene, topPane);
        StackPane.setAlignment(topPane, Pos.CENTER);
        StackPane.setAlignment(subScene, Pos.CENTER);

        // setup scene and stage:
        final Scene scene = new Scene(stackPane, 1000, 1000);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        // add mouse handling
        addMouseHanderToScene(scene, cameraRotateX, cameraRotateY, cameraTranslate);

        // put some objects into the world:

        // setup PhongMaterial with external map material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image("resources/axe_low_turned_initialShadingGroup_Diffuse.png"));
        material.setBumpMap(new Image("resources/axe_low_turned_initialShadingGroup_Normal.png"));
        material.setSpecularMap(new Image("resources/axe_low_turned_initialShadingGroup_Speck.png"));

        // load model (fxml) into meshview
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("resources/Axe_test.fxml"));
        MeshView meshView = fxmlLoader.<MeshView>load();

        // set scale of the meshview model
        meshView.setScaleX(scale);
        meshView.setScaleY(scale);
        meshView.setScaleZ(scale);


        // add material to meshview
        meshView.setMaterial(material);

        // add meshView to viewer
        world.getChildren().add(meshView);


        // change scale via mouse scroll event
        scene.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            scale = scale + deltaY;

            meshView.setScaleX(scale);
            meshView.setScaleY(scale);
            meshView.setScaleZ(scale);
        });


        // Create ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        // MenuItem actived "DrawMode.FILL"
        MenuItem item1 = new MenuItem("DrawMode.FILL");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                meshView.setDrawMode(DrawMode.FILL);
            }
        });

        // MenuItem actived "DrawMode.LINE"
        MenuItem item2 = new MenuItem("DrawMode.LINE");
        item2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                meshView.setDrawMode(DrawMode.LINE);
            }
        });

        // MenuItem PointLight Off
        MenuItem item3 = new MenuItem("PointLight Off");
        item3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                pointLight.setColor(Color.BLACK);
            }
        });

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(new SeparatorMenuItem(), item1, item2, new SeparatorMenuItem(), item3);


        // show ContextMenu when user right-click on screen
        scene.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(world, event.getScreenX(), event.getScreenY());
            }
        });

    }

    private double mouseDownX;
    private double mouseDownY;

    /**
     * handle mouse events to navigate in viewer
     *
     * @param scene
     * @param cameraTranslate
     */
    private void addMouseHanderToScene(Scene scene, Rotate cameraRotateX, Rotate cameraRotateY, Translate cameraTranslate) {
        scene.setOnMousePressed((me) -> {
            mouseDownX = me.getSceneX();
            mouseDownY = me.getSceneY();
        });
        scene.setOnMouseDragged((me) -> {
            double mouseDeltaX = me.getSceneX() - mouseDownX;
            double mouseDeltaY = me.getSceneY() - mouseDownY;

            if (me.isShiftDown()) {
                cameraTranslate.setZ(cameraTranslate.getZ() + mouseDeltaY);
            } else // rotate
            {
                cameraRotateY.setAngle(cameraRotateY.getAngle() + mouseDeltaX);
                cameraRotateX.setAngle(cameraRotateX.getAngle() - mouseDeltaY);
            }
            mouseDownX = me.getSceneX();
            mouseDownY = me.getSceneY();
        });
    }


}

