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
                        "nom": "BNP Paribas",
                        "code_banque": 30004,
                        "site_internet": "https://mabanque.bnpparibas"
                    },
                    {
                        "nom": "Société Générale",
                        "code_banque": 30003,
                        "site_internet": "https://particuliers.societegenerale.fr"
                    },
                    {
                        "nom": "Crédit Agricole",
                        "code_banque": 30006,
                        "site_internet": "https://www.credit-agricole.fr"
                    },
                    {
                        "nom": "Crédit Agricole (Nord)",
                        "code_banque": 16706,
                        "site_internet": "https://www.credit-agricole.fr"
                    },
                    {
                        "nom": "BPCE",
                        "code_banque": 30002,
                        "site_internet": "https://www.bpce.fr"
                    },
                    {
                        "nom": "Crédit Mutuel",
                        "code_banque": 10278,
                        "site_internet": "https://www.creditmutuel.fr"
                    },
                    {
                        "nom": "La Banque Postale",
                        "code_banque": 20041,
                        "site_internet": "https://www.labanquepostale.fr"
                    },
                    {
                        "nom": "HSBC France",
                        "code_banque": 30056,
                        "site_internet": "https://www.hsbc.fr"
                    },
                    {
                        "nom": "CIC",
                        "code_banque": 30087,
                        "site_internet": "https://www.cic.fr"
                    },
                    {
                        "nom": "Banque Populaire",
                        "code_banque": 18789,
                        "site_internet": "https://www.banquepopulaire.fr"
                    },
                    {
                        "nom": "Crédit du Nord",
                        "code_banque": 30076,
                        "site_internet": "https://www.credit-du-nord.fr"
                    },
                    {
                        "nom": "LCL",
                        "code_banque": 30002,
                        "site_internet": "https://www.lcl.fr"
                    },
                    {
                        "nom": "Boursorama Banque",
                        "code_banque": 40618,
                        "site_internet": "https://www.boursorama-banque.com"
                    },
                    {
                        "nom": "ING Direct",
                        "code_banque": 30088,
                        "site_internet": "https://www.ing.fr"
                    },
                    {
                        "nom": "Fortuneo",
                        "code_banque": 10278,
                        "site_internet": "https://www.fortuneo.fr"
                    },
                    {
                        "nom": "Caisse d'Épargne",
                        "code_banque": 14564,
                        "site_internet": "https://www.caisse-epargne.fr"
                    },
                    {
                        "nom": "Crédit Foncier",
                        "code_banque": 30009,
                        "site_internet": "https://www.creditfoncier.fr"
                    },
                    {
                        "nom": "Revolut",
                        "code_banque": 28233,
                        "site_internet": "https://www.revolut.com/fr-FR"
                    },
                    {
                        "nom": "Hello Bank",
                        "code_banque": 30109,
                        "site_internet": "https://www.hellobank.fr"
                    },
                    {
                        "nom": "Monabanq",
                        "code_banque": 14106,
                        "site_internet": "https://www.monabanq.com"
                    },
                    {
                        "nom": "BforBank",
                        "code_banque": 10907,
                        "site_internet": "https://www.bforbank.com"
                    },
                    {
                        "nom": "Orange Bank",
                        "code_banque": 10928,
                        "site_internet": "https://www.orangebank.fr"
                    },
                    {
                        "nom": "Nickel",
                        "code_banque": 15589,
                        "site_internet": "https://nickel.eu"
                    },
                    {
                        "nom": "Axa Banque",
                        "code_banque": 10948,
                        "site_internet": "https://www.axa.fr"
                    },
                    {
                        "nom": "Crédit Coopératif",
                        "code_banque": 10938,
                        "site_internet": "https://www.credit-cooperatif.coop"
                    },
                    {
                        "nom": "La Nef",
                        "code_banque": 12609,
                        "site_internet": "https://www.lanef.com"
                    }
                ]
            );

        } else {

        }
    } catch (error) {
        console.error('Erreur lors de la synchronisation:', error);
    }
}
syncDatabase()


module.exports = BankInfo;