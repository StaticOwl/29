import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // Change this to your server's URL

export const joinGame = createAsyncThunk(
    'game/join',
    async (playerId, { rejectWithValue }) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/join?playerId=${playerId}`);
            return response.data;
        } catch (error) {
            return rejectWithValue('Error joining game');
        }
    }
);

export const makeMove = createAsyncThunk(
    'game/move',
    async ({ playerId, move }, { rejectWithValue }) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/move?playerId=${playerId}&move=${move}`);
            return response.data;
        } catch (error) {
            return rejectWithValue('Error making move');
        }
    }
);

const gameSlice = createSlice({
    name: 'game',
    initialState: {
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
            .addCase(joinGame.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(joinGame.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.messages.push(action.payload);
            })
            .addCase(joinGame.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload;
                state.messages.push(action.payload);
            })
            .addCase(makeMove.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(makeMove.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.messages.push(action.payload);
            })
            .addCase(makeMove.rejected, (state, action) => {
                state.status = 'failed';
                state.error = action.payload;
                state.messages.push(action.payload);
            });
    },
});

export const { setPlayerId } = gameSlice.actions;

export default gameSlice.reducer;