Tetris Battle Bot
=================

Automatically plays tetris battle using CV and AI.
Written in Java.

[Video of it in action](http://youtu.be/8LKVLxygN5o)

### How it works
This works by using Java's Robot class to take screenshots of the game while 
it's happening, find the board, compute the best move to make, and move the
piece to the best position. This even supports holding pieces to make the best
decision.

### More details

On startup, the program looks for the tetris board. After it finds it, it will
only ever take a small screenshot of just the board to look for the piecies.
It then samples just a corner of each cell to determine what color is there and
essentially copy the current state of the board into a representation it can
understand.

After this, the program tries all positions of the current piece to find the
"best" one as scored by a function that determines how much better the new board
will be compared to the old one. Once it has found the best position (or piece
to use if it swaps the other in), it computes a list of moves to send to the 
window to move the piece to the right spot. These list of moves are what you
see in the console on the left of the YouTube video. 

The video is a little slow mainly due to recording overhead but also because
the sending of keys back to the game is extremely finnicky. I tried many 
iterations of using large vs. small delays between key ups and key downs, but
it was always extremely unreliable.

