import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Container, TextField, Button, Paper, Typography, Select, MenuItem } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { createGame } from '../redux/gameSlice';
import '../App.css';

const CreateGameForm = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { gameId, status, playerId } = useSelector((state) => state.game);
    const [gameName, setGameName] = useState('');
    const [cardBack, setCardBack] = useState('');

    const handleCreateGame = () => {
        dispatch(createGame({ gameName, cardBack, playerId }));
    };

    useEffect(() => {
        if (status === 'succeeded' && gameId) {
            navigate('/game');
        }
    }, [status, gameId, navigate]);

    return (
        <Container className="container">
            <Typography variant="h4" component="h1" gutterBottom>
                Create Game
            </Typography>
            <Paper className="paper">
                <TextField
                    label="Game Name"
                    value={gameName}
                    onChange={(e) => setGameName(e.target.value)}
                    fullWidth
                    margin="dense"
                    className="textField"
                />
                <Select
                    label="Card Back"
                    value={cardBack}
                    onChange={(e) => setCardBack(e.target.value)}
                    fullWidth
                    margin="dense"
                    className="select"
                >
                    <MenuItem value="back1">Card Back 1</MenuItem>
                    <MenuItem value="back2">Card Back 2</MenuItem>
                    <MenuItem value="back3">Card Back 3</MenuItem>
                </Select>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleCreateGame}
                    fullWidth
                    className="button"
                >
                    Create Game
                </Button>
            </Paper>
        </Container>
    );
};

export default CreateGameForm;
