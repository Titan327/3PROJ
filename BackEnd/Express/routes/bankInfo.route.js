const router = require('express').Router();
const securityMiddleware = require('../security/middleware.security')
const bankInfo = require('../controllers/bankInfo.controller')

//GET http://localhost:9000/api/bankInfo/
router.get("/",securityMiddleware.verifyIsAuth,bankInfo.Get);


module.exports = router;
