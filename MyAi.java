import connectK.CKPlayer;
import connectK.BoardModel;
   
import java.awt.Point;
import java.lang.Math;

public class MyAi extends CKPlayer {
	//Board[col][row]

	public MyAi(byte player, BoardModel state) {
		super(player, state);
		teamName = "MyAi";
	}

	@Override
	public Point getMove(BoardModel state) {
		
		//Create array to store alphas of possible moves
		int[] moves = new int[state.getWidth()];
		
		//Store alphas for each possible move
		for (int i = 0; i < moves.length; i++){
			Point nextMove = testPoint(state, i);
			if (nextMove == null){
				moves[i] = 0;
				continue;
			}
			moves[i] = minimax(state.placePiece(nextMove, this.player), 0, this.player, false);
		}
		
		//store best alpha
		int bestAlpha = -1000; //-infinity
		Point bestAlphaPoint = null;
		
		for (int i = 0; i < moves.length; i++){
			if (moves[i] > bestAlpha){
				bestAlpha = moves[i];
				bestAlphaPoint = testPoint(state, i);
			}
		}
		
		for (int alpha : moves)
			System.out.println(alpha);


		return bestAlphaPoint;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}

	
	public int eval(BoardModel state){
		//Get move to evaluate
		Point lastMove = state.getLastMove();
		//Total evaluation (alpha)
		int alpha = 0;

		//Row Heuristic - Check consecutive columns in same row.
		//If no opposing player within klength, add weight
		//k^n, where n is the number of checkers within klength
		boolean noOpp = true;
		boolean flag = false;
		int n = 0;
		int rowRating = 0;
		
		boolean inRange;
		if (state.getWidth() - lastMove.getX() >= state.getkLength())
			inRange = true;
		else
			inRange = false;
		
		
		//This for loop checks next 4 slots to see if they are free or they have its token
		//If slot has its token, increment n
		for (int i = (int)lastMove.getX() + 1; i < state.getWidth() && inRange && noOpp && i < (int)lastMove.getX() + state.getkLength(); i++){
			if (state.getSpace(i, (int)lastMove.getY()) != state.getSpace(lastMove)
					&& state.getSpace(i,  (int)lastMove.getY()) != 0){
				noOpp = false;
			}
			else{
				if (state.getSpace(i, (int)lastMove.getY()) == 0){
					//do nothing
				}
				else{ //then space must be same player token
					n++;
				}
			}
			flag = true;
		}
		
		//Checks to see if no opposing token && if the loop was accessed above
		if (noOpp && flag)
			rowRating = (int)Math.pow(state.getkLength(), n); //better to use power func or loop? error double to int
		else
			rowRating = 0;
		
		
		
		
		
		
		
		
		//Near Center heuristic?
		//The farther away, the less points you gain
		int median = state.getWidth()/2;
		int centerPoints = 8;
		int difference = Math.abs((int)lastMove.getX() - median);
		int centerRating = 0;
		double mod = difference * 1.0 / state.getWidth();
		
		centerRating = (int)(centerPoints * (1+(1-mod)));
		
		
		
		
		//Need to implement a vertical heuristic to balance the horizontal heuristic!!!
		
		
		
		//Add all heuristic values
		alpha = rowRating + centerRating; //add more heuristics later
		
		return alpha;
	}
	
	public int minimax(BoardModel state, int depth, byte player, boolean maximizingPlayer){

		//Set player variable to set next move
		byte p;
		if (player == 1)
			p = 2;
		else
			p = 1;
			


		
		//If depth = 0 or terminal state, return heuristic value
		if (depth == 0) //add condition to check terminal state
			return eval(state);
		
		if (maximizingPlayer){
			int bestVal = -1000; //technically -infinity
			
			//Call minimax for next possible moves
			for (int i = 0; i < state.getWidth(); i++){
				Point nextMove = testPoint(state, i);
				//If there are no available moves in this current column, move to next column
				if (nextMove == null)
					continue;
				
				BoardModel nextBoard = state.placePiece(nextMove, p);
				int val = minimax(nextBoard, depth - 1, p, false);
				bestVal = Math.max(bestVal, val);
			}
			
			return bestVal;
		}
		else{
			int bestVal = 1000; //technically +infinity
			for (int i = 0; i < state.getWidth(); i++){
				Point nextMove = testPoint(state, i);
				//If there are no available moves in this current column, move to next column
				if (nextMove == null)
					continue;
				
				BoardModel nextBoard = state.placePiece(nextMove, p);
				int val = minimax(nextBoard, depth - 1, p, true);
				bestVal = Math.min(bestVal, val);
			}
			
			return bestVal;
		}
	}



	//Returns the point where we want to place the checker in certain column
	public Point testPoint(BoardModel state, int column){
		for (int i = 0; i < state.height; i++)
			if (state.getSpace(column, i) == 0)
				return new Point(column, i);
		return null;
	}

}
