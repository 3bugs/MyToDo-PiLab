//Load HTTP module
const http = require("http");
const port = 3000;

const server = http.createServer((req, res) => {
    res.statusCode = 200;
    //res.setHeader('Content-Type', 'text/plain');
    res.setHeader('Content-Type', 'application/json');

    let dataList = [];
    dataList.push({
        firstName: 'Promlert',
        lastName: 'Loviciht',
        age: 45,
        birthDate: '1974-11-21',
    });
    dataList.push({
        firstName: 'Aaa',
        lastName: 'Bbb',
        age: 20,
        birthDate: '1999-01-01',
    });
    res.end(JSON.stringify({
        error: {
            code: 0,
            message: 'อ่านข้อมูลสำเร็จ',
        },
        dataList: dataList,
    }));
});

//listen for request on port 3000, and as a callback function have the port listened on logged
server.listen(port, () => {
    console.log(`Server running at port ${port}`);
});