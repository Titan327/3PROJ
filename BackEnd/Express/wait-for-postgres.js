const net = require('net');

const host = process.argv[2];
const port = parseInt(process.argv[3], 10);

const waitForPostgres = async () => {
    return new Promise((resolve) => {
        const checkConnection = () => {
            const socket = net.connect(port, host);

            socket.on('connect', () => {
                console.log(`PostgreSQL is available on ${host}:${port}`);
                socket.end();
                resolve();
            });

            socket.on('error', () => {
                console.log(`Waiting for Postgres`);
                setTimeout(checkConnection, 1000);
            });
        };

        checkConnection();
    });
};

waitForPostgres().then(() => process.exit(0));
