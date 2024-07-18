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
        <Container className="container">
            <Typography variant="h4" component="h1" gutterBottom>
                Enter Player ID
            </Typography>
            <Paper className="paper">
                <TextField
                    label="Player ID"
                    value={playerId}
                    onChange={(e) => setPlayerIdInput(e.target.value)}
                    fullWidth
                    margin="normal"
                    className="textField"
                />
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSubmit}
                    fullWidth
                    className="button"
                >
                    Submit
                </Button>
            </Paper>
        </Container>
    );
};

export default PlayerIdForm;
