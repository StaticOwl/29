import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import PlayerIdForm from './components/PlayerIdForm';
import MainMenu from './components/MainMenu';
import CreateGameForm from './components/CreateGameForm';
import GamePage from './components/GamePage';
import { useSelector } from 'react-redux';
import './App.css';

function App() {
    const playerId = useSelector((state) => state.game.playerId);
    const gameId = useSelector((state) => state.game.gameId);

    return (
        <Router>
            <Routes>
                <Route path="/" element={<PlayerIdForm />} />
                <Route
                    path="/menu"
                    element={playerId ? <MainMenu /> : <Navigate to="/" />}
                />
                <Route
                    path="/create"
                    element={playerId ? <CreateGameForm /> : <Navigate to="/" />}
                />
                <Route
                    path="/game"
                    element={gameId ? <GamePage /> : <Navigate to="/menu" />}
                />
            </Routes>
        </Router>
    );
}

export default App;
