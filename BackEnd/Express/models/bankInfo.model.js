const mongoose = require('mongoose');


const bankInfoSchema = new mongoose.Schema({
    nom: String,
    code_banque: Number,
    site_internet: String,
}, { versionKey: false });


const BankInfo = mongoose.model('BankInfo', bankInfoSchema);

async function syncDatabase() {
    try {
        const count = await BankInfo.countDocuments();
        if (count === 0) {
            BankInfo.insertMany([
                {
                    nom: "BNP Paribas",
                    code_banque: 30004,
                    site_internet: "https://mabanque.bnpparibas"
                },
                {
                    nom: "Société Générale",
                    code_banque: 30003,
                    site_internet: "https://particuliers.societegenerale.fr"
                },
                {
                    nom: "Crédit Agricole",
                    code_banque: 30006,
                    site_internet: "https://www.credit-agricole.fr"
                },
                {
                    nom: "BPCE",
                    code_banque: 30002,
                    site_internet: "https://www.bpce.fr"
                },
                {
                    nom: "Crédit Mutuel",
                    code_banque: 10278,
                    site_internet: "https://www.creditmutuel.fr"
                },
                {
                    nom: "La Banque Postale",
                    code_banque: 20041,
                    site_internet: "https://www.labanquepostale.fr"
                },
                {
                    nom: "HSBC France",
                    code_banque: 30056,
                    site_internet: "https://www.hsbc.fr"
                },
                {
                    nom: "CIC",
                    code_banque: 30087,
                    site_internet: "https://www.cic.fr"
                },
                {
                    nom: "Banque Populaire",
                    code_banque: 18789,
                    site_internet: "https://www.banquepopulaire.fr"
                },
                {
                    nom: "Crédit du Nord",
                    code_banque: 30076,
                    site_internet: "https://www.credit-du-nord.fr"
                },
                {
                    nom: "LCL",
                    code_banque: 30002,
                    site_internet: "https://www.lcl.fr"
                },
                {
                    nom: "Boursorama Banque",
                    code_banque: 12521,
                    site_internet: "https://www.boursorama-banque.com"
                },
                {
                    nom: "ING Direct",
                    code_banque: 30088,
                    site_internet: "https://www.ing.fr"
                },
                {
                    nom: "Fortuneo",
                    code_banque: 10278,
                    site_internet: "https://www.fortuneo.fr"
                },
                {
                    nom: "Caisse d'Épargne",
                    code_banque: 14564,
                    site_internet: "https://www.caisse-epargne.fr"
                },
                {
                    nom: "Crédit Foncier",
                    code_banque: 30009,
                    site_internet: "https://www.creditfoncier.fr"
                }
            ]);
            console.log('BankInfo est initialisé.');
        } else {
            console.log('BankInfo contient déjà des données.');
        }
    } catch (error) {
        console.error('Erreur lors de la synchronisation:', error);
    }
}
syncDatabase()


module.exports = BankInfo;