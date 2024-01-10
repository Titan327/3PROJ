const Sequelize = require('sequelize');

const sequelize = new Sequelize(process.env.DB_NAME, process.env.DB_USER, process.env.DB_PSWD, {
    host: process.env.DB_HOST,
    dialect: process.env.DB_DIALECT,
});

// Test de la connexion à la base de données
sequelize
    .authenticate()
    .then(() => {
        console.log('Connexion à la base de données établie avec succès.');
    })
    .catch(err => {
        console.error('Erreur de connexion à la base de données:', err);
    });


module.exports = sequelize;