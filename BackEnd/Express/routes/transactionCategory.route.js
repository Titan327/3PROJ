const router = require('express').Router();
const TransactionCategory = require('../controllers/TransactionCategory.controller');
const securityMiddleware = require("../security/middleware.security");

router.get("/",securityMiddleware.verifyIsAuth, TransactionCategory.getAllTrasactionCategory);

module.exports = router;
