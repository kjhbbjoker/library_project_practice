import { useState, useEffect } from 'react';

function SimpleApp() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetch('/api/books')
      .then(res => res.json())
      .then(data => {
        setBooks(data.content || []);
        setLoading(false);
      })
      .catch(err => {
        setError('API 연결 실패: ' + err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="p-4">로딩중...</div>;
  if (error) return <div className="p-4 text-red-500">{error}</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto p-4">
        <h1 className="text-3xl font-bold text-blue-600 mb-6">📚 WSA MES Library</h1>
        
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">도서 목록</h2>
          {books.length === 0 ? (
            <p className="text-gray-500">도서가 없습니다.</p>
          ) : (
            <div className="grid gap-4">
              {books.slice(0, 10).map((book: any) => (
                <div key={book.id} className="border p-4 rounded-lg hover:bg-gray-50">
                  <h3 className="font-semibold text-lg">{book.name}</h3>
                  <p className="text-gray-600">저자: {book.author}</p>
                  <p className="text-gray-500 text-sm">ISBN: {book.isbn}</p>
                  <span className={`inline-block px-2 py-1 rounded text-xs ${
                    book.available ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {book.available ? '대출 가능' : '대출 중'}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default SimpleApp;
