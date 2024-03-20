const google = require("../Oauth2/google.oauth2");
const authPassport = require("../configurations/passport.config");
const router = require('express').Router();

//PUBLIC
//GET http://localhost:9002/api/oauth2/google/register
router.get("/google",authPassport.authenticate('google', { scope: ['email', 'profile'] }));
//GET http://localhost:9002/api/oauth2/google/callback
router.get("/google/callback",google.callback);

module.exports = router;