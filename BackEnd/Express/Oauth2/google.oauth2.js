const { OAuth2Client } = require('google-auth-library');
const User = require('../models/user.model');
const {createToken} = require("../security/token.security");



const oauthGoogle = async (req,res) => {
    const { token } = req.body;

    try {
        const ticket = await client.verifyIdToken({
            idToken: token,
            audience: '1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com',
        });
        const payload = ticket.getPayload();
        const userId = payload['sub'];

        console.log(payload);


        res.send('Token ID Google vérifié avec succès');
    } catch (error) {
        console.error('Erreur de vérification du token ID Google :', error);
        res.status(401).send('Token ID Google invalide');
    }
}

module.exports = {
    oauthGoogle
}