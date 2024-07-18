import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axiosInstance from '../api/axiosConfig';

export const createGame = createAsyncThunk(
    'game/create',
    async ({ gameName, cardBack, playerId }, { rejectWithValue, dispatch }) => {
        try {
            const response = await axiosInstance.post('/game/create', { gameName, cardBack, playerId });
            const gameId = response.data.gameId;
            await dispatch(joinGame({ gameId, playerId }));
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

export const fillWithBots = createAsyncThunk(
    'game/fillWithBots',
    async (gameId, { rejectWithValue }) => {
        try {
            const response = await axiosInstance.post(`/game/${gameId}/fillWithBots`);
            return response.data;
        } catch (error) {
            return rejectWithValue('Error filling game with bots');
        }
    }
);

export const startGame = createAsyncThunk(
    'game/start',
    async (gameId, { rejectWithValue }) => {
        try {
            const response = await axiosInstance.post(`/game/${gameId}/start`);
            return response.data;
        } catch (error) {
            return rejectWithValue('Error starting game');
        }
    }
);

const gameSlice = createSlice({
    name: 'game',
    initialState: {
        gameId: null,
        playerId: '',
        players: [],
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
                state.gameId = action.payload.gameId;
                state.players = [state.playerId];
                state.status = 'succeeded';
                state.messages.push(`Game created with ID: ${state.gameId}`);
            })
            .addCase(joinGame.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.players = action.payload.players;
                state.messages.push(`Joined game: ${state.gameId}`);
            })
            .addCase(makeMove.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.messages.push(action.payload.message);
            })
            .addCase(fillWithBots.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.players = action.payload.players;
                state.messages.push('Filled game with bots');
            })
            .addCase(startGame.fulfilled, (state, action) => {
                state.status = 'succeeded';
                state.messages.push('Game started');
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
