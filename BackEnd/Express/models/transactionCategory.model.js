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
    icon: {
        type: DataTypes.STRING,
        allowNull: false
    },
    color: {
        type: DataTypes.STRING,
        allowNull: false
    }
});

async function syncDatabase() {
    try {
        const count = await TransactionCategoryModel.count();
        if (count === 0) {

            await TransactionCategoryModel.bulkCreate([
                { label: 'Alimentation', icon: 'ramen_dining', color: '#EBB734' },
                { label: 'Divertissement', icon: 'attractions', color: '#0BA339' },
                { label: 'Voyages', icon: 'luggage', color: '#0B61A3'},
                { label: 'Cadeaux', icon: 'featured_seasonal_and_gifts', color: '#D90000' },
                { label: 'Courses', icon: 'shopping_cart', color: '#9B989C' },
                { label: 'Activités sportives', icon: 'sports_football', color: '#D67900' },
                { label: 'Sorties', icon: 'sports_bar', color: '#C49F67' },
                { label: 'Soins personnels', icon: 'healing', color: '#C47C67'},
                { label: 'Événements spéciaux', icon: 'celebration', color: '#A114A3'}

            ]);
            console.log('Données de base ajoutées avec succès.');
        } else {
            console.log('La table TransactionCategoryModel contient déjà des données.');
        }
    } catch (error) {
        console.error('Erreur lors de la synchronisation:', error);
    }
}

sequelize.sync().then(() => {
    console.log('TransactionModel table created successfully!');
    syncDatabase();
}).catch((error) => {
    console.error('Unable to create table UserModel : TransactionModel', error);
});


module.exports = TransactionCategoryModel;