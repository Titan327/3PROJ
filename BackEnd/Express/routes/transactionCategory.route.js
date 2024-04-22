const router = require('express').Router();
const TransactionCategory = require('../controllers/transactionCategory.controller');
const securityMiddleware = require("../security/middleware.security");

//PUBLIC
//GET http://localhost:9002/api/transactionCategories
router.get("/",securityMiddleware.verifyIsAuth, TransactionCategory.getTrasactionCategories);

module.exports = router;
