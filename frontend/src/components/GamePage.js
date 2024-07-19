import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Container, Button, Paper, Typography, List, ListItem, ListItemText } from '@mui/material';
import { fillWithBots, startGame } from '../redux/gameSlice';
import '../App.css';

const GamePage = () => {
    const dispatch = useDispatch();
    const { gameId, playerId, players } = useSelector((state) => state.game);
    console.log(players);

    return (
        <Container className="game-container">
            <Paper className="game-center">
                <Typography variant="h4" component="h1" gutterBottom>
                    Game ID: {gameId}
                </Typography>
                <Typography variant="h6" gutterBottom>
                    Player: {playerId}
                </Typography>
                <Paper className="player-positions">
                    {players.length > 0 && (
                        <div className="player-list">
                            <List>
                                {players.map((player, index) => (
                                    <ListItem key={index} className={`player-${index}`}>
                                        <ListItemText primary={player} />
                                    </ListItem>
                                ))}
                            </List>
                        </div>
                    )}
                    {players.length < 4 && (
                        <Typography variant="body1" gutterBottom>
                            Waiting for other players...
                        </Typography>
                    )}
                    {players.length < 4 && (
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={() => dispatch(fillWithBots(gameId))}
                            fullWidth
                            className="button"
                        >
                            Fill with Bots
                        </Button>
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
            </Paper>
        </Container>
    );
};

export default GamePage;
