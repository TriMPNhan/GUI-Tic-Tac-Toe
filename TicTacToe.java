import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * 
 * @author Tri Nhan
 * @version 1.01, November 2nd, 2017
 */

public class TicTacToe implements ActionListener
{
    public static final String PLAYER_X = "X"; // player using "X"
    public static final String PLAYER_O = "O"; // player using "O"
    public static final String EMPTY = " ";  // empty cell
    public static final String TIE = "T"; // game ended in a tie
    
    private String player;   // current player (PLAYER_X or PLAYER_O)
    
    private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress
    
    private int xWins;
    private int oWins;
    
    private int numFreeSquares; // number of squares still free
    
    private String board[][]; // 3x3 array representing the board
    
    private JLabel status; // text area to print game status
   
    private ArrayList<JButton> bList; // List of tic tac toe buttons
    
    private JMenuItem quitItem; // Quit option
    private JMenuItem newItem; // Reset/create a new game
   
    private JLabel xTally; // Tallies for amount of wins
    private JLabel oTally;
   
    private JTextField xDisplay;  //The actual value display
    private JTextField oDisplay;
   
    private JButton resetGameButton; //Button to reset the game
    private JButton resetAllButton; //Button to reset the game and tallies
    
    /** 
     * Constructs a new Tic-Tac-Toe GUI
     */
    public TicTacToe()
    {
        board = new String[3][3]; // initialize board and the value of other fields values
        bList = new ArrayList<JButton>();
        numFreeSquares = 9;
        player = PLAYER_X;
        
        
        JFrame tFrame = new JFrame("Tic Tac Toe");
       tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //quit program when closing
       
       Container contentPane = tFrame.getContentPane();
       contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); // Box layout to lay 
                                                                            //out everything vertically
       
       JMenuBar menuBar = new JMenuBar(); 
       tFrame.setJMenuBar(menuBar); // Add menu bar to frame
       
       //Create the menu and add it to the menu bar
       JMenu fileMenu = new JMenu("File"); 
       menuBar.add(fileMenu);
       
       //Create menu item "Quit" and add to the menu
       quitItem = new JMenuItem("Quit");
       fileMenu.add(quitItem);
       
       newItem = new JMenuItem("New/Reset Game");
       fileMenu.add(newItem);
       
       // Create keyboard shortcut for quit
       final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
       quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
       newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
       
       // Listen for quit selection and create a anonymous inner class to place code for quit here
       quitItem.addActionListener(new ActionListener() 
       {
           public void actionPerformed(ActionEvent event)
           {
               System.exit(0);
           }
        }
       );
       // Listen for new game selection coded in same manner as above
       newItem.addActionListener(new ActionListener() 
       {
           public void actionPerformed(ActionEvent event)
           {
               clearBoard();
           }
        }
       );
       
       
       
       // Create layout for the Tallies
       JPanel tallyPanel = new JPanel();
       tallyPanel.setLayout(new GridLayout(2, 2));
       
       // Create Labels
       xTally = new JLabel("Player X wins");
       xTally.setVerticalAlignment(JLabel.BOTTOM);
       xTally.setHorizontalAlignment(JLabel.CENTER);
       tallyPanel.add(xTally);
       
       oTally = new JLabel("Player O wins");
       oTally.setVerticalAlignment(JLabel.BOTTOM);
       oTally.setHorizontalAlignment(JLabel.CENTER);
       tallyPanel.add(oTally);
       
       
       // Displays current amount of wins for each player
       xDisplay = new JTextField(3);
       xDisplay.setEditable(false); // This value cannot be edited
       xDisplay.setFont(new Font(null, Font.BOLD, 14));
       xDisplay.setHorizontalAlignment(JTextField.CENTER);
       tallyPanel.add(xDisplay);
       
       xDisplay.setText(String.valueOf(xWins)); // Set display to x win variable
       
       oDisplay = new JTextField(3);
       oDisplay.setEditable(false);
       oDisplay.setFont(new Font(null, Font.BOLD, 14));
       oDisplay.setHorizontalAlignment(JTextField.CENTER);
       tallyPanel.add(oDisplay);
       
       oDisplay.setText(String.valueOf(oWins)); // Set display to o win variable
       
       contentPane.add(tallyPanel); // Add the Tallies to the content Pane
       
       
       
       
       // Create tic tac toe layout for the actual game
       JPanel tPanel = new JPanel();
       tPanel.setLayout(new GridLayout(3, 3));
       
       // For loop to continously add the buttons to the arraylist for the tic tac toe GUI
       for (int i = 0; i < 9; i++)
       {
           bList.add(new JButton(""));
           tPanel.add(bList.get(i));
           bList.get(i).addActionListener(this);
        }
       
       tPanel.setPreferredSize(new Dimension(300, 300)); // Set size of the actual game and add it
       contentPane.add(tPanel);
       
       
       
       // Ignore this was trying to play around with images, will prob add myself to a future version
       // Turns out trying nicely add a strike through the images is a lot more work than expected
       //BufferedImage x = ImageIO.read(new File("images/X.png"));
       
       
       // Status report for the game condition
       status = new JLabel(getWinner());
       status.setHorizontalAlignment(JLabel.CENTER);
       contentPane.add(status);
       
       
       
       
       // Create button layout 
       JPanel buttonPanel = new JPanel();
       buttonPanel.setLayout(new GridLayout(1, 2));
       
       resetGameButton = new JButton("Reset Game");
       buttonPanel.add(resetGameButton);
       
       resetAllButton = new JButton("Reset Everything");
       buttonPanel.add(resetAllButton);
       
       contentPane.add(buttonPanel);
       
       // Register buttons as listeners
       resetGameButton.addActionListener(this);
       resetAllButton.addActionListener(this);

       
       
       
       clearBoard(); // Clear the board to set it up
       tFrame.setResizable(false); // Don't let the board to be resized to look too ugly
       tFrame.setVisible(true);
       tFrame.pack();
    }
    
    /**
     * Sets everything up for a new game.  Marks all squares in the Tic Tac Toe board as empty,
     * and indicates no winner yet, 9 free squares and the current player is player X.
     */
   private void clearBoard()
   {
       for (int i = 0; i < 3; i++) {
           for (int j = 0; j < 3; j++) {
               board[i][j] = EMPTY;
               bList.get(3 * i + j).setEnabled(true);
               bList.get(3 * i + j).setText("");
         }
       }
       winner = EMPTY;
       numFreeSquares = 9;
       player = PLAYER_X;     // Player X always has the first turn.
   }


   /**
    * Plays the game of Tic Tac Toe.
    */

   public void playGame(int r, int c)
   {
         board[r][c] = player;        // fill in the square with player
         numFreeSquares--;            // decrement number of free squares

         // see if the game is over
         if (haveWinner(r, c))
         {
            winner = player; // must be the player who just went
            for (int i = 0; i < 9; i++)
            {
                bList.get(i).setEnabled(false);
            }
         }
            
         else if (numFreeSquares==0) 
         {
            winner = TIE; // board is full so it's a tie
         }
         
         //sets the text of the buttons to be X or O depending on the turn after it is pressed
         if (player.equals(PLAYER_X))
         {
            bList.get(3 * r + c).setText("X");
         }
         else
         {
             bList.get(3 * r + c).setText("O");
         }
         
         // change to other player (this won't do anything if game has ended)
         if (player==PLAYER_X) 
            player=PLAYER_O;
         else 
            player=PLAYER_X;
      

   } 


   /**
    * Returns true if filling the given square gives us a winner, and false
    * otherwise.
    *
    * @param int row of square just set
    * @param int col of square just set
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(int row, int col) 
   {
       // unless at least 5 squares have been filled, we don't need to go any further
       // (the earliest we can have a winner is after player X's 3rd move).

       if (numFreeSquares>4) return false;

       // Note: We don't need to check all rows, columns, and diagonals, only those
       // that contain the latest filled square.  We know that we have a winner 
       // if all 3 squares are the same, as they can't all be blank (as the latest
       // filled square is one of them).

       // check row "row"
       if ( board[row][0].equals(board[row][1]) &&
            board[row][0].equals(board[row][2]) ) return true;
       
       // check column "col"
       if ( board[0][col].equals(board[1][col]) &&
            board[0][col].equals(board[2][col]) ) return true;

       // if row=col check one diagonal
       if (row==col)
          if ( board[0][0].equals(board[1][1]) &&
               board[0][0].equals(board[2][2]) ) return true;

       // if row=2-col check other diagonal
       if (row==2-col)
          if ( board[0][2].equals(board[1][1]) &&
               board[0][2].equals(board[2][0]) ) return true;

       // no winner yet
       return false;
   }

   
   
  
   /**
    * returns the winner or status of the current game
    * 
    * @return String of game status
    */
    public String getWinner()
   {
       String temp = "";
       if (winner == PLAYER_X)
       {
            xWins += 1;
            temp = "\nPlayer X is the winner!\n";
       }
       else if (winner == PLAYER_O)
       {
            oWins += 1;
            temp = "\nPlayer O is the winner!\n";
       }   
       else if (winner == TIE)
       {
            temp = "\nGame is tied.\n";
       }
       else
       {
           temp = "\nGame is ongoing...\n";
       }
       return temp;
   }
   
   /**
    * Code for what happens when the buttons are pressed
    */
   public void actionPerformed(ActionEvent e)
   {
       Object o = e.getSource(); // Get the action
       
       if (o instanceof JButton) // See if it's a button which is always but the conditional is placed in case
       {                         // of future code
            JButton button = (JButton)o;
            
            if (button == resetGameButton) // Resets the game board
            {
                clearBoard();
            }
            else if (button == resetAllButton) // Resets the game board the and the tallies
            {
                clearBoard();
                xWins = 0;
                oWins = 0;
            }
            else // Logic for when a tic tac toe button is pressed
            {
                int r = bList.indexOf(button) / 3; // Floor division by 3 to get row
                int c = bList.indexOf(button) % 3; // Modular division to gram column
                playGame(r, c);
                bList.get(3 * r + c).setEnabled(false); // Disables the button so that the user cannot press
            }                                           // it multiple times per match
       }
       
       // Update the display
       status.setText(getWinner());
       xDisplay.setText(String.valueOf(xWins));
       oDisplay.setText(String.valueOf(oWins));
   }
}

