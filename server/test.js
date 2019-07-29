const http = require("http");
const port = 3000;

const server = http.createServer((req, res) => {
    res.statusCode = 200;
    res.setHeader('Content-Type', 'application/json');

    const data = [];
    data.push({
        firstName: 'Promlert',
        lastName: 'Lovichit',
        age: 44,
    });
    data.push({
        firstName: 'Aaaaaa',
        lastName: 'Bbbbbb',
        age: 99,
    });

    const output = {
        error: {
            code: 0,
            message: 'อ่านข้อมูลสำเร็จ',
            log: '',
        },
        data,
    };

    res.end(JSON.stringify(output));
});

server.listen(port, () => {
    console.log(`Server running at port ${port}`);
});
