const { DataTypes } = require('sequelize');
const sequelize = require('../configurations/db.config');

const TransactionCategoryModel = sequelize.define("TransactionCategory", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    label: {
        type: DataTypes.STRING,
        allowNull: false
    },
});

async function syncDatabase() {
    try {
        await sequelize.sync();
        console.log('Base de données synchronisée.');

        const count = await TransactionCategoryModel.count();
        if (count === 0) {

            await TransactionCategoryModel.bulkCreate([
                { label: 'Alimentation' },
                { label: 'Divertissement' },
                { label: 'Voyages' },
                { label: 'Cadeaux' },
                { label: 'Courses' },
                { label: 'Activités sportives' },
                { label: 'Sorties' },
                { label: 'Soins personnels' },
                { label: 'Événements spéciaux' }

            ]);
            console.log('Données de base ajoutées avec succès.');
        } else {
            console.log('La table Test contient déjà des données.');
        }
    } catch (error) {
        console.error('Erreur lors de la synchronisation:', error);
    }
}

syncDatabase();

module.exports = TransactionCategoryModel;