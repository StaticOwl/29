import React from 'react';
import { Container, Button, Paper, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const MainMenu = () => {
    const navigate = useNavigate();

    return (
        <Container className="container">
            <Typography variant="h4" component="h1" gutterBottom>
                Main Menu
            </Typography>
            <Paper className="paper">
                <Button
                    variant="contained"
                    color="primary"
                    onClick={() => navigate('/create')}
                    fullWidth
                    className="button"
                >
                    Create Game
                </Button>
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={() => navigate('/join')}
                    fullWidth
                    className="button"
                >
                    Join Game
                </Button>
            </Paper>
        </Container>
    );
};

export default MainMenu;
