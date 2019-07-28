const express = require('express');
const app = express();
const mysql = require('mysql');
const bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({   // to support URL-encoded bodies
    extended: true
}));

const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'pilab'
});
db.connect(err => {
    if (err) {
        throw err;
    }
    console.log('Database connected as id ' + db.threadId);
});

app.get('/', (req, res) => {
    let dataList = [];
    dataList.push({
        firstName: 'Promlert',
        lastName: 'Loviciht',
        age: 45,
        birthDate: '1974-11-21',
    });
    dataList.push({
        firstName: 'Aaa123999',
        lastName: 'Bbb456999',
        age: 20,
        birthDate: '1999-01-01',
    });
    //res.set('Content-Type', 'application/json');
    res.send({
        error: {
            code: 0,
            message: 'อ่านข้อมูลสำเร็จ',
        },
        dataList: dataList,
    });
});

app.post('/api/todo/get', (req, res) => {
    db.query(
        'SELECT * FROM todo',
        (err, results, fields) => {
            if (err) {
                res.send({
                    error: {
                        code: 1,
                        message: 'เกิดข้อผิดพลาดในการเข้าถึงฐานข้อมูล'
                    },
                    dataList: null
                });
            } else {
                res.send({
                    error: {
                        code: 0
                    },
                    dataList: results
                });
            }
        });
});

app.post('/api/todo/add', (req, res) => {
    db.query(
        'INSERT INTO todo (title, details) VALUES (?, ?)',
        [req.body.title, req.body.details],
        (err, results, fields) => {
            if (err) {
                res.send({
                    error: {
                        code: 1,
                        message: 'เกิดข้อผิดพลาดในการเข้าถึงฐานข้อมูล'
                    }
                });
            } else {
                res.send({
                    error: {
                        code: 0,
                        message: 'เพิ่มข้อมูลสำเร็จ'
                    }
                });
            }
        });
});

app.listen(8000, () => {
    console.log('Example app listening on port 8000!')
});
