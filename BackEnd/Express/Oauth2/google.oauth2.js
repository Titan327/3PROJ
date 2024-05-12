const passport = require("passport");
const User = require('../models/user.model');
const {createToken} = require("../security/token.security");

const callback = async (req, res) => {
    console.log("la");

    passport.authenticate('google', async function(err, user, info) {
        if (err) {

            console.log(err);

            return res.status(500).send({ message: "Une erreur s'est produite lors de l'authentification"});
        }
        if (!user) {

            return res.status(401).send({ message: "Échec de l'authentification"});
        }


        const email = user._json.email;

        const exist = await User.findOne({ where: { email: email } });

        if (exist){
            console.log("trouvé");
            const token = createToken(exist);
            console.log(token);

            return res.send(`
                <script>
                 window.opener.postMessage({token:'${token}'}, '*');
                 window.close();
                </script>
            `);
        }
        console.log("pas trouvé");


        const firstname = user._json.name

        let lastname;
        if (user._json.familyName){
            lastname = user._json.familyName;
        }else {
            lastname = user._json.name;
        }

        const username = user._json.given_name

        let birth_date;
        if (user._json.birth_date){
            birth_date = user._json.birth_date;
        }else {
            birth_date = null;
        }

        const newUser = await User.create({
            firstname,
            lastname,
            username,
            email,
            birth_date,
            password:null
        });

        const token = createToken(newUser);

        return res.send(`
            <script>
             window.opener.postMessage({token:'${token}'}, '*');
             window.close();
            </script>
        `);


    })(req, res);




}


module.exports = {
    callback
}