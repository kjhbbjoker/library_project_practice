import { useState, useEffect } from 'react';
import { Loan, PageResponse, Book, User } from '../types';
import { loanApi, bookApi, userApi } from '../services/api';
import { format } from 'date-fns';
import LoanForm from './LoanForm';

const LoanList: React.FC = () => {
  const [loans, setLoans] = useState<PageResponse<Loan> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<'ACTIVE' | 'RETURNED' | 'OVERDUE' | ''>('');
  const [currentPage, setCurrentPage] = useState(0);
  const [showForm, setShowForm] = useState(false);

  const fetchLoans = async (page: number = 0) => {
    try {
      setLoading(true);
      const response = await loanApi.getLoans({
        status: statusFilter || undefined,
        page,
        size: 10,
        sort: 'loanDate,desc'
      });
      setLoans(response.data);
      setCurrentPage(page);
      setError(null);
    } catch (err) {
      setError('대출 목록을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLoans();
  }, [statusFilter]);

  const handleReturn = async (loanId: number) => {
    if (!confirm('반납 처리하시겠습니까?')) return;
    
    try {
      await loanApi.returnBook(loanId);
      fetchLoans(currentPage);
    } catch (err: any) {
      alert(err.response?.data?.message || '반납 처리에 실패했습니다.');
    }
  };

  const handleFormSuccess = () => {
    setShowForm(false);
    fetchLoans(currentPage);
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">대출 중</span>;
      case 'RETURNED':
        return <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">반납 완료</span>;
      case 'OVERDUE':
        return <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">연체</span>;
      default:
        return <span className="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-800">{status}</span>;
    }
  };

  if (loading && !loans) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg">로딩중...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">대출 관리</h2>
        <button
          onClick={() => setShowForm(true)}
          className="bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700"
        >
          + 새 대출 등록
        </button>
      </div>

      {/* Filter Form */}
      <div className="bg-white p-4 rounded-lg shadow">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              상태 필터
            </label>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as any)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">전체</option>
              <option value="ACTIVE">대출 중</option>
              <option value="RETURNED">반납 완료</option>
              <option value="OVERDUE">연체</option>
            </select>
          </div>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Loans Table */}
      {loans && (
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  도서 정보
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  사용자
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  대출 정보
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  상태
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  작업
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loans.content.map((loan) => (
                <tr key={loan.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {loan.book.name}
                      </div>
                      <div className="text-sm text-gray-500">
                        저자: {loan.book.author}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {loan.user.name}
                      </div>
                      <div className="text-sm text-gray-500">
                        {loan.user.email}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div>대출일: {format(new Date(loan.loanDate), 'yyyy-MM-dd')}</div>
                    <div>반납예정: {format(new Date(loan.dueDate), 'yyyy-MM-dd')}</div>
                    {loan.returnDate && (
                      <div>반납일: {format(new Date(loan.returnDate), 'yyyy-MM-dd')}</div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {getStatusBadge(loan.status)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    {loan.status === 'ACTIVE' && (
                      <button
                        onClick={() => handleReturn(loan.id)}
                        className="text-green-600 hover:text-green-900"
                      >
                        반납 처리
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Pagination */}
          {loans.totalPages > 1 && (
            <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200">
              <div className="flex-1 flex justify-between sm:hidden">
                <button
                  onClick={() => fetchLoans(currentPage - 1)}
                  disabled={loans.first}
                  className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                >
                  이전
                </button>
                <button
                  onClick={() => fetchLoans(currentPage + 1)}
                  disabled={loans.last}
                  className="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                >
                  다음
                </button>
              </div>
              <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm text-gray-700">
                    총 <span className="font-medium">{loans.totalElements}</span>개 중{' '}
                    <span className="font-medium">{currentPage * loans.size + 1}</span>-
                    <span className="font-medium">
                      {Math.min((currentPage + 1) * loans.size, loans.totalElements)}
                    </span>개 표시
                  </p>
                </div>
                <div>
                  <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
                    {Array.from({ length: loans.totalPages }, (_, i) => (
                      <button
                        key={i}
                        onClick={() => fetchLoans(i)}
                        className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium ${
                          i === currentPage
                            ? 'z-10 bg-primary-50 border-primary-500 text-primary-600'
                            : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                        }`}
                      >
                        {i + 1}
                      </button>
                    ))}
                  </nav>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Loan Form Modal */}
      {showForm && (
        <LoanForm
          onSuccess={handleFormSuccess}
          onCancel={() => setShowForm(false)}
        />
      )}
    </div>
  );
};

export default LoanList;
