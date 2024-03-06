const router = require('express').Router();
const TransactionController = require('../controllers/transaction.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//GET http://localhost:9002/api/transaction/{id}
router.get("/:id", securityMiddleware.verifyIsAuth, TransactionController.getTransaction);
//POST http://localhost:9002/api/transaction/{id}
router.post("/", securityMiddleware.verifyIsAuth, TransactionController.createTransaction);



module.exports = router;
