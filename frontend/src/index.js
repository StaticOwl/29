import React from 'react';
import {createRoot} from 'react-dom/client';
import {Provider} from 'react-redux';
import CssBaseline from '@mui/material/CssBaseline';
import {ThemeProvider} from '@mui/material/styles';
import {createTheme} from '@mui/material/styles';
import {store} from './redux/store';
import App from './App';
import {DevSupport} from "@react-buddy/ide-toolbox";
import {ComponentPreviews, useInitial} from "./dev";

const theme = createTheme();
const cors = require("cors");

const container = document.getElementById('root');
const root = createRoot(container);

root.render(
    <React.StrictMode>
        <Provider store={store}>
            <ThemeProvider theme={theme}>
                <CssBaseline/>
                <DevSupport ComponentPreviews={ComponentPreviews}
                            useInitialHook={useInitial}
                >
                    <App/>
                </DevSupport>
            </ThemeProvider>
        </Provider>
    </React.StrictMode>
);