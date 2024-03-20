const passportConfig = require("passport");
const {Strategy: GoogleStrategy} = require("passport-google-oauth20");
const passport = require("passport");
const User = require("../models/user.model");
const {createToken} = require("../security/token.security");

GOOGLE_CLIENT_ID = "1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com";
GOOGLE_CLIENT_SECRET = "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";

passport.use(new GoogleStrategy({
        clientID: GOOGLE_CLIENT_ID,
        clientSecret: GOOGLE_CLIENT_SECRET,
        callbackURL: "http://localhost:9002/api/oauth2/google/callback",
        passReqToCallback: true
    },
    function(request,accessToken, refreshToken, profile, done) {

        return done(null, profile);

    }
));


module.exports = passportConfig;