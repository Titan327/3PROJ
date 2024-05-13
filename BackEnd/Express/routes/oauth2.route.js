const google = require("../Oauth2/google.oauth2");

const router = require('express').Router();

//PUBLIC
//GET http://localhost:9002/api/oauth2/google/register

//GET http://localhost:9002/api/oauth2/google/
router.get("/google",google.oauthGoogle);

module.exports = router;