import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Container, TextField, Button, Paper, Typography } from '@mui/material';
import { setPlayerId } from '../redux/gameSlice';
import '../App.css';

const PlayerIdForm = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [playerId, setPlayerIdInput] = useState('');

    const handleSubmit = () => {
        dispatch(setPlayerId(playerId));
        navigate('/menu');
    };

    return (
        <Container className="player-id-container">
            <Paper className="player-id-paper">
                <Typography variant="h4" component="h1" gutterBottom className="welcome-text">
                    Welcome to Game 29
                </Typography>
                <div className="form-row">
                    <TextField
                        label="Player ID"
                        value={playerId}
                        onChange={(e) => setPlayerIdInput(e.target.value)}
                        fullWidth
                        margin="dense"
                        className="textField"
                    />
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSubmit}
                        className="button"
                    >
                        Start Playing
                    </Button>
                </div>
            </Paper>
        </Container>
    );
};

export default PlayerIdForm;
