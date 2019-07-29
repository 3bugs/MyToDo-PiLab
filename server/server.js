const express = require('express');
const server = express();
const mysql = require('mysql');
const bodyParser = require('body-parser');

// parse application/x-www-form-urlencoded
server.use(bodyParser.urlencoded({ extended: false }));
// parse application/json
server.use(bodyParser.json());

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
});

server.get('/get_todo', (req, res) => {
    setTimeout(() => {
        db.query(
            'SELECT * FROM todo',
            (err, data, fields) => {
                if (err) {
                    res.send({
                        error: {
                            code: 1,
                            message: 'เกิดข้อผิดพลาดในการเข้าถึงฐานข้อมูล',
                            log: '',
                        },
                        data: null,
                    });
                } else {
                    for (let i = 0; i < data.length; i++) {
                        const item = data[i];
                        let {finished} = item;
                        finished = finished === 1;
                        item.finished = finished;
                    }

                    res.send({
                        error: {
                            code: 0,
                            message: 'อ่านข้อมูลสำเร็จ',
                            log: '',
                        },
                        data,
                    });
                }
            }
        );
    }, 1000);
});

server.post('/add_todo', (req, res) => {
    setTimeout(() => {
        db.query(
            'INSERT INTO todo (title, details, due_date) VALUES (?, ?, ?)',
            [req.body.title, req.body.details, req.body.due_date],
            (err, results, fields) => {
                if (err) {
                    res.send({
                        error: {
                            code: 1,
                            message: 'เกิดข้อผิดพลาดในการเข้าถึงฐานข้อมูล',
                            log: '',
                        },
                    });
                } else {
                    res.send({
                        error: {
                            code: 0,
                            message: 'บันทึกข้อมูลสำเร็จ',
                            log: '',
                        },
                    });
                }
            }
        );
    }, 1000);
});

server.post('/update_todo', (req, res) => {
    setTimeout(() => {
        db.query(
            'UPDATE todo SET title = ?, details = ?, finished = ?, due_date = ? WHERE id = ?',
            [req.body.title, req.body.details, req.body.finished, req.body.due_date, req.body.id],
            (err, results, fields) => {
                if (err) {
                    res.send({
                        error: {
                            code: 1,
                            message: 'เกิดข้อผิดพลาดในการเข้าถึงฐานข้อมูล',
                            log: '',
                        },
                    });
                } else {
                    res.send({
                        error: {
                            code: 0,
                            message: 'บันทึกข้อมูลสำเร็จ',
                            log: '',
                        },
                    });
                }
            }
        );
    }, 1000);
});

server.post('/delete_todo', (req, res) => {
    setTimeout(() => {
        db.query(
            'DELETE FROM todo WHERE id = ?',
            [req.body.id],
            (err, results, fields) => {
                if (err) {
                    res.send({
                        error: {
                            code: 1,
                            message: 'เกิดข้อผิดพลาดในการเข้าถึงฐานข้อมูล',
                            log: '',
                        },
                    });
                } else {
                    res.send({
                        error: {
                            code: 0,
                            message: 'บันทึกข้อมูลสำเร็จ',
                            log: '',
                        },
                    });
                }
            }
        );
    }, 1000);
});

server.listen(3000, () => {
    console.log('Server listening at port 3000');
});