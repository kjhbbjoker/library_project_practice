import { useState, useEffect } from 'react';
import { Book, User, PageResponse } from '../types';
import { loanApi, bookApi, userApi } from '../services/api';

interface LoanFormProps {
  onSuccess: () => void;
  onCancel: () => void;
}

const LoanForm: React.FC<LoanFormProps> = ({ onSuccess, onCancel }) => {
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [selectedBookId, setSelectedBookId] = useState<number | null>(null);
  const [users, setUsers] = useState<User[]>([]);
  const [books, setBooks] = useState<Book[]>([]);
  const [userKeyword, setUserKeyword] = useState('');
  const [bookKeyword, setBookKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 사용자 검색
  const searchUsers = async (keyword: string) => {
    try {
      const response = await userApi.getUsers({
        keyword,
        size: 10
      });
      setUsers(response.data.content);
    } catch (err) {
      console.error('사용자 검색 실패:', err);
    }
  };

  // 도서 검색 (대출 가능한 도서만)
  const searchBooks = async (keyword: string) => {
    try {
      const response = await bookApi.getBooks({
        keyword,
        size: 10
      });
      // 대출 가능한 도서만 필터링
      const availableBooks = response.data.content.filter(book => book.available);
      setBooks(availableBooks);
    } catch (err) {
      console.error('도서 검색 실패:', err);
    }
  };

  useEffect(() => {
    if (userKeyword) {
      const timer = setTimeout(() => {
        searchUsers(userKeyword);
      }, 300);
      return () => clearTimeout(timer);
    } else {
      setUsers([]);
    }
  }, [userKeyword]);

  useEffect(() => {
    if (bookKeyword) {
      const timer = setTimeout(() => {
        searchBooks(bookKeyword);
      }, 300);
      return () => clearTimeout(timer);
    } else {
      setBooks([]);
    }
  }, [bookKeyword]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedUserId || !selectedBookId) {
      setError('사용자와 도서를 모두 선택해주세요.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await loanApi.createLoan(selectedUserId, selectedBookId);
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || '대출 등록에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const selectedUser = users.find(u => u.id === selectedUserId);
  const selectedBook = books.find(b => b.id === selectedBookId);

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            새 대출 등록
          </h3>
          
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* 사용자 선택 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                사용자 검색 *
              </label>
              <input
                type="text"
                value={userKeyword}
                onChange={(e) => setUserKeyword(e.target.value)}
                placeholder="사용자 이름 또는 이메일"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-primary-500 focus:border-primary-500"
              />
              {users.length > 0 && (
                <div className="mt-2 max-h-40 overflow-y-auto border border-gray-200 rounded-md">
                  {users.map((user) => (
                    <div
                      key={user.id}
                      onClick={() => {
                        setSelectedUserId(user.id);
                        setUserKeyword(user.name);
                        setUsers([]);
                      }}
                      className={`p-2 cursor-pointer hover:bg-gray-50 ${
                        selectedUserId === user.id ? 'bg-primary-50' : ''
                      }`}
                    >
                      <div className="text-sm font-medium">{user.name}</div>
                      <div className="text-xs text-gray-500">{user.email}</div>
                    </div>
                  ))}
                </div>
              )}
              {selectedUser && (
                <div className="mt-2 p-2 bg-green-50 border border-green-200 rounded">
                  <div className="text-sm font-medium text-green-800">
                    선택된 사용자: {selectedUser.name}
                  </div>
                </div>
              )}
            </div>

            {/* 도서 선택 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                도서 검색 *
              </label>
              <input
                type="text"
                value={bookKeyword}
                onChange={(e) => setBookKeyword(e.target.value)}
                placeholder="도서 제목 또는 저자"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-primary-500 focus:border-primary-500"
              />
              {books.length > 0 && (
                <div className="mt-2 max-h-40 overflow-y-auto border border-gray-200 rounded-md">
                  {books.map((book) => (
                    <div
                      key={book.id}
                      onClick={() => {
                        setSelectedBookId(book.id);
                        setBookKeyword(book.name);
                        setBooks([]);
                      }}
                      className={`p-2 cursor-pointer hover:bg-gray-50 ${
                        selectedBookId === book.id ? 'bg-primary-50' : ''
                      }`}
                    >
                      <div className="text-sm font-medium">{book.name}</div>
                      <div className="text-xs text-gray-500">저자: {book.author}</div>
                    </div>
                  ))}
                </div>
              )}
              {selectedBook && (
                <div className="mt-2 p-2 bg-green-50 border border-green-200 rounded">
                  <div className="text-sm font-medium text-green-800">
                    선택된 도서: {selectedBook.name}
                  </div>
                </div>
              )}
            </div>

            {error && (
              <div className="text-red-600 text-sm">{error}</div>
            )}

            <div className="flex justify-end space-x-2 pt-4">
              <button
                type="button"
                onClick={onCancel}
                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50"
              >
                취소
              </button>
              <button
                type="submit"
                disabled={loading || !selectedUserId || !selectedBookId}
                className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 disabled:opacity-50"
              >
                {loading ? '등록중...' : '대출 등록'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoanForm;
