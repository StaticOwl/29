import React, { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container, Typography, TextField, Button, List, ListItem, ListItemText, Paper } from '@mui/material';
import { setPlayerId, createGame, joinGame, makeMove } from './redux/gameSlice';

function App() {
    const dispatch = useDispatch();
    const { gameId, playerId, messages, status } = useSelector((state) => state.game);
    const [move, setMove] = useState('');
    const [joinGameId, setJoinGameId] = useState('');

    const handleCreateGame = () => {
        dispatch(createGame());
    };

    const handleJoinGame = () => {
        dispatch(joinGame({ gameId: joinGameId, playerId }));
    };

    const handleMakeMove = () => {
        dispatch(makeMove({ gameId, playerId, move }));
        setMove('');
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
                    onClick={handleCreateGame}
                    fullWidth
                    disabled={status === 'loading'}
                >
                    Create New Game
                </Button>
            </Paper>
            <Paper sx={{ padding: '20px', marginBottom: '20px' }}>
                <TextField
                    label="Game ID"
                    value={joinGameId}
                    onChange={(e) => setJoinGameId(e.target.value)}
                    fullWidth
                    margin="normal"
                />
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleJoinGame}
                    fullWidth
                    disabled={status === 'loading'}
                >
                    Join Game
                </Button>
            </Paper>
            {gameId && (
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
                        color="primary"
                        onClick={handleMakeMove}
                        fullWidth
                        disabled={status === 'loading'}
                    >
                        Make Move
                    </Button>
                </Paper>
            )}
            <Paper>
                <List>
                    {messages.map((message, index) => (
                        <ListItem key={index}>
                            <ListItemText primary={message} />
                        </ListItem>
                    ))}
                </List>
            </Paper>
        </Container>
    );
}

export default App;