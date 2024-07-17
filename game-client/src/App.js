import React, { useState, useTransition, Suspense } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container, Typography, TextField, Button, List, ListItem, ListItemText, Paper, CircularProgress } from '@mui/material';
import { setPlayerId, joinGame, makeMove } from './redux/gameSlice';

// Simulating a lazy-loaded component
const LazyGameHistory = React.lazy(() => new Promise(resolve => {
  setTimeout(() => {
    resolve({
      default: ({ messages }) => (
          <List>
            {messages.map((message, index) => (
                <ListItem key={index}>
                  <ListItemText primary={message} />
                </ListItem>
            ))}
          </List>
      )
    });
  }, 1000);
}));

function App() {
  const dispatch = useDispatch();
  const { playerId, messages, status } = useSelector((state) => state.game);
  const [move, setMove] = useState('');
  const [isPending, startTransition] = useTransition();

  const handleJoinGame = () => {
    dispatch(joinGame(playerId));
  };

  const handleMakeMove = () => {
    dispatch(makeMove({ playerId, move }));
    setMove('');
  };

  const handleShowHistory = () => {
    startTransition(() => {
      // In a real app, this might trigger some expensive computation or data fetching
      console.log("Showing game history...");
    });
  };

  return (
      <Container maxWidth="sm">
        <Typography variant="h4" component="h1" gutterBottom>
          Game Client
        </Typography>
        <Paper sx={{ padding: '20px', marginBottom: '20px' }}>
          <TextField
              label="Player ID"
              value={playerId}
              onChange={(e) => dispatch(setPlayerId(e.target.value))}
              fullWidth
              margin="normal"
          />
          <Button
              variant="contained"
              color="primary"
              onClick={handleJoinGame}
              fullWidth
              disabled={status === 'loading'}
          >
            Join Game
          </Button>
        </Paper>
        <Paper sx={{ padding: '20px', marginBottom: '20px' }}>
          <TextField
              label="Move"
              value={move}
              onChange={(e) => setMove(e.target.value)}
              fullWidth
              margin="normal"
          />
          <Button
              variant="contained"
              color="secondary"
              onClick={handleMakeMove}
              fullWidth
              disabled={status === 'loading'}
          >
            Make Move
          </Button>
        </Paper>
        <Button
            variant="outlined"
            onClick={handleShowHistory}
            fullWidth
            disabled={isPending}
        >
          {isPending ? 'Loading History...' : 'Show Game History'}
        </Button>
        <Paper sx={{ marginTop: '20px' }}>
          <Suspense fallback={<CircularProgress />}>
            <LazyGameHistory messages={messages} />
          </Suspense>
        </Paper>
      </Container>
  );
}

export default App;