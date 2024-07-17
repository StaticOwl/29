// src/redux/gameSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../api/axiosConfig';

// Remove this line as we're now using the axiosInstance
// const API_BASE_URL = 'http://localhost:8080';

export const createGame = createAsyncThunk(
    'game/create',
    async (_, { rejectWithValue }) => {
        try {
            const response = await axiosInstance.get('/game/create');
            return response.data;
        } catch (error) {
            return rejectWithValue('Error creating game');
        }
    }
);

export const joinGame = createAsyncThunk(
    'game/join',
    async ({ gameId, playerId }, { rejectWithValue }) => {
        try {
            const response = await axiosInstance.get(`/game/${gameId}/join?playerId=${playerId}`);
            return response.data;
        } catch (error) {
            return rejectWithValue('Error joining game');
        }
    }
);

export const makeMove = createAsyncThunk(
    'game/move',
    async ({ gameId, playerId, move }, { rejectWithValue }) => {
        try {
            const response = await axiosInstance.get(`/game/${gameId}/move?playerId=${playerId}&move=${move}`);
            return response.data;
        } catch (error) {
            return rejectWithValue('Error making move');
        }
    }
);

const gameSlice = createSlice({
    name: 'game',
    initialState: {
        gameId: null,
        playerId: '',
        messages: [],
        status: 'idle',
        error: null,
    },
    reducers: {
        setPlayerId: (state, action) => {
            state.playerId = action.payload;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(createGame.fulfilled, (state, action) => {
                state.gameId = action.payload.split(': ')[1];
                state.status = 'succeeded';
                state.messages.push(`Game created with ID: ${state.gameId}`);
            })
            .addCase(joinGame.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.messages.push(action.payload);
            })
            .addCase(makeMove.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.messages.push(action.payload);
            })
            .addMatcher(
                action => action.type.endsWith('/pending'),
                state => {
                    state.status = 'loading';
                }
            )
            .addMatcher(
                action => action.type.endsWith('/rejected'),
                (state, action) => {
                    state.status = 'failed';
                    state.error = action.payload;
                    state.messages.push(action.payload);
                }
            );
    },
});

export const { setPlayerId } = gameSlice.actions;

export default gameSlice.reducer;