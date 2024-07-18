import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container, Button, Paper, Typography, List, ListItem, ListItemText } from '@mui/material';
import { fillWithBots, startGame } from '../redux/gameSlice';
import '../App.css';

const GamePage = () => {
    const dispatch = useDispatch();
    const { gameId, playerId, players } = useSelector((state) => state.game);

    return (
        <Container className="container">
            <Typography variant="h4" component="h1" gutterBottom>
                Game ID: {gameId}
            </Typography>
            <Typography variant="h6" gutterBottom>
                Player: {playerId}
            </Typography>
            <Paper className="paper">
                <Typography variant="h6" gutterBottom>
                    Players:
                </Typography>
                <List className="list">
                    {players.map((player, index) => (
                        <ListItem key={index}>
                            <ListItemText primary={player} />
                        </ListItem>
                    ))}
                </List>
                {players.length < 4 && (
                    <>
                        <Typography variant="body1" gutterBottom>
                            Waiting for other players...
                        </Typography>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={() => dispatch(fillWithBots(gameId))}
                            fullWidth
                            className="button"
                        >
                            Fill with Bots
                        </Button>
                    </>
                )}
                {players.length === 4 && (
                    <Button
                        variant="contained"
                        color="secondary"
                        onClick={() => dispatch(startGame(gameId))}
                        fullWidth
                        className="button"
                    >
                        Start Game
                    </Button>
                )}
            </Paper>
        </Container>
    );
};

export default GamePage;
