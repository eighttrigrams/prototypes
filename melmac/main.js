const { app, BrowserWindow } = require('electron');
const { default: installExtension, REACT_DEVELOPER_TOOLS } = require('electron-devtools-installer');


function createWindow () {
    // Erstelle das Browser-Fenster.
    let mainWindow = new BrowserWindow({
        width: 776,
        minWidth: 776,
        minHeight: 558,
        height: 558,
        webPreferences: {
            nodeIntegration: true
        }
    });

    mainWindow.loadURL('file://' + __dirname + '/resources/public/index.html');

    installExtension(REACT_DEVELOPER_TOOLS)
        .then((name) => console.log(`Added Extension:  ${name}`))
        .catch((err) => console.log('An error occurred: ', err));
}

app.on('ready', createWindow);