const router = require('express').Router();
const securityMiddleware = require('../security/middleware.security')
const bankInfo = require('../controllers/bankInfo.controller')

//GET http://localhost:9000/api/bankInfo/all
router.get("/all",securityMiddleware.verifyIsAuth,bankInfo.Get);
//GET http://localhost:9000/api/bankInfo/
router.get("/:code_banque",securityMiddleware.verifyIsAuth,bankInfo.GetByCodeBanque);


module.exports = router;
