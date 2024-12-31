package asteroassignment;
 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
 
public class Main extends Application {
	
	//var used to track the type of shape actively being created by the selected button
	//using a string here may be slightly less space efficient, but allows for easy scaling by adding other shapes, etc.
	String shapeType = "";
	
	
	//var used to track the currently selected shape for movement or deletion
	Shape selected;
	
	//variables to assist in correctly placing the shape on the canvas
	double shapeStartingX = 0;
	double shapeStartingY = 0;
	
	//state variable used to assist with flow and transition of features
	boolean drawing = false;
	
	//AnchorPane used as the base for this exercise
    AnchorPane root = new AnchorPane();
	
    
    //for this exercise, I've placed the vast majority of the code inside the start function. I'm unsure whether your architecture
    //uses FXML + controllers, or whether all events are created on launch. My previous experiences uses the former, while this implementation
    //uses the latter.
    @Override
    public void start(Stage primaryStage) {
    	//adding UI elements (buttons, information text) to window
        Button RectButton = new Button();
        RectButton.setText("Rectangle");
        RectButton.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
            	shapeType = "Rectangle";
            }
        });
        
        //top ui elements are stored in an HBox
        HBox buttons = new HBox();
        buttons.setSpacing(5);
        
        Button CircButton = new Button();
        CircButton.setText("Circle");
        CircButton.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
               shapeType = "Circle";
            }
        });
        
        //a quick label to inform the user on the ways they may interact with the program
        Label infoText = new Label();
        infoText.setText("Click and drag to create shapes. Press F to delete");
        
        buttons.getChildren().addAll(RectButton, CircButton, infoText);

        root.getChildren().add(buttons);
        AnchorPane.setTopAnchor(buttons, null);
        
        
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler <MouseEvent>() {
        	
        	//on the mouse clicking down, track the coordinates of that press to be used later in shape drawing
        	@Override
        	public void handle(MouseEvent e) {
        		if (e.getButton() == MouseButton.PRIMARY) {
        			shapeStartingX = e.getSceneX();
        			shapeStartingY = e.getSceneY();
        			
        			//if I click on something that is not the AnchorPane (a shape), set that shape to be the selected shape for dragging
        			//or deletion
        			if (e.getPickResult().getIntersectedNode() != root) {
        				selected = (Shape) e.getPickResult().getIntersectedNode();
        			}
        			else {
        				drawing = true;
        			}
        		}
        	}
        });
        
        //on mouse release, create a shape based on selected button
        root.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler <MouseEvent>() {
        	@Override
        	public void handle(MouseEvent e) {
        		if (e.getButton() == MouseButton.PRIMARY) {
        			
        			//create the shape
        			createShape(e);
    				//color is undeclared, so it remains the default (black)
        		}
        		
        	}
        });
        
        
      //setting F as delete key
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {
        	
        	@Override
        	public void handle(KeyEvent e) {
        		if(e.getCode() == KeyCode.F) {
        			root.getChildren().remove(selected);
        		}
        	}
        });
        
        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
public void createShape(MouseEvent e) {
	
	//declare a generic shape to be instantiated in the desired way based on the selected shape type
	Shape s;
	
	//if I am in the 'drawing' state, i.e. I have previously clicked on the AnchorPane, then create the shape
	if (drawing) {
		drawing = false;
		
		
		//height and width are determined by the difference between the start and end coords
		int width = (int) Math.abs(shapeStartingX - e.getSceneX());
		int height = (int) Math.abs(shapeStartingY - e.getSceneY());
		
		if (shapeType.equals("Rectangle")) {
			
			s = new Rectangle(shapeStartingX, shapeStartingY, width, height);
			//top-left corner of the shape needs to be adjusted based on the difference of mouse cursor between starting point
			//and end point. If you drag up to create a shape, the top-left corner is relative to the ending mouse position
			
			//if the end mouse position is above the starting mouse position
			if (e.getSceneY() < shapeStartingY) {
				
				//and to the left of the starting mouse position
				if (e.getSceneX() < shapeStartingX) {
					
				//ending coordinate should be relative to the ending x and y coords
				s.relocate((int)e.getSceneX(), (int)e.getSceneY());
				}
				
				else {
					//if only above, retain the original x coordinate but use the ending y coordinate
					s.relocate(shapeStartingX, (int)e.getSceneY());
				}
			}
			
			//if to the left, but not above
			else if (e.getSceneX() < shapeStartingX) {
				s.relocate((int)e.getSceneX(), shapeStartingY);
			}
		}
		else if (shapeType.equals("Circle")) {
			
			//The construction for the circle shall be done by using the midpoint between the starting and end points as the center
			double midX = (shapeStartingX + e.getSceneX()) / 2;
			double midY = (shapeStartingY + e.getSceneY()) / 2;
			
			// and the radius shall be the larger value between the difference of the x's and y's
			
			double xDist = Math.abs(shapeStartingX - e.getSceneX());
			double yDist = Math.abs(shapeStartingY - e.getSceneY());
			
			double radius = Math.max(xDist, yDist);
			
			//the scaling size of the circle does not seem to perfectly match the mouse cursor, so I've scaled the radius by
			//a factor of 0.7 to better represent the start and end points for the relative size range of shapes for this
			//exercise. In a more thorough implementation, this issue would be resolved on construction of shape rather than placement on canvas
			
			s = new Circle(midX, midY, radius * 0.7);
			
			
			
		}
		//if I am not in the drawing state, set the previously declared shape to null as to not interfere with event handling
		else {
			s = null;
		}
		//if the shape actually exists, then we should add the drag-event to it
		if (s != null) {
			s.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler <MouseEvent>() {
	        	
				@Override
				public void handle(MouseEvent e) {
	        		
					//need to find offset between shape pos and mouse pos and adjust position accordingly, shape will always be up-left
					//from drag position, so can subtract both
					
					//the above does not seem to work as intended due to the shape being initialized to scene position (0,0) for one tick.
					//for a quick but still cleaner solution, I've simply aligned the shape to put the cursor at the center.
					
					s.relocate(e.getSceneX() - (width / 2), e.getSceneY() - (height / 2));
					selected = s;
				}	
			});
			//on creation of a new shape, select it as well
				selected = s;
				root.getChildren().add(s);
			}
		}
		//color is undeclared, so it remains the default (black)
	}
	
	
	
	

 public static void main(String[] args) {
        launch(args);
    }
}