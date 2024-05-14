const { OAuth2Client } = require('google-auth-library');
const User = require('../models/user.model');
const {createToken} = require("../security/token.security");
const Token = require("../security/token.security");



const oauthGoogle = async (req,res) => {
    const { token } = req.body;

    try {
        const client = new OAuth2Client('1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com');

        const ticket = await client.verifyIdToken({
            idToken: token,
            audience: '1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com',
        });
        const payload = ticket.getPayload();

        const email = payload['email'];
        const given_name = payload['given_name'];
        const family_name = payload['family_name'];
        let name = payload['name'];

        const emailAlreadyExist = await User.findOne({where: { email: email }});
        let usernameAlreadyExist = await User.findOne({where: { username: name }});

        if (!emailAlreadyExist){
            while (!usernameAlreadyExist){
                name = randomPseudo();
                usernameAlreadyExist = await User.findOne({where: { username: name }});
            }

            const user = await User.create({
                given_name,
                family_name,
                name,
                email
            });

            const token = Token.createToken(user);
            return res.status(200).send({token:token,oauth:"true"});

        }else {
            const user = await User.findOne({where: { email: email }});
            const token = Token.createToken(user);

            if (user.password === null){
                return res.status(200).send({token:token,oauth:"true"});
            }else {
                return res.status(200).send({token:token,oauth:"false"});
            }

        }
    } catch (error) {
        res.status(500).send('internal server error');
    }
}

function randomPseudo () {
    const adjs = ['happy', 'funny', 'clever', 'smart', 'brave', 'kind', 'gentle', 'silly', 'lucky', 'wise','sad'];
    const noms = ['unicorn', 'penguin', 'banana', 'elephant', 'dolphin', 'robot', 'ninja', 'pirate', 'wizard', 'dragon','cat'];

    const adj = adjs[Math.floor(Math.random() * adjs.length)];
    const nom = noms[Math.floor(Math.random() * noms.length)];
    return adj+" "+nom;

}

module.exports = {
    oauthGoogle
}