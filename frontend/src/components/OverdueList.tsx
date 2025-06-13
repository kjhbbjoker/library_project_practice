import { useState, useEffect } from 'react';
import { Loan } from '../types';
import { loanApi } from '../services/api';
import { format, differenceInDays } from 'date-fns';

const OverdueList: React.FC = () => {
  const [overdueLoans, setOverdueLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchOverdueLoans = async () => {
    try {
      setLoading(true);
      // 먼저 연체 상태 업데이트
      await loanApi.updateOverdueLoans();
      // 그 다음 연체 목록 조회
      const response = await loanApi.getOverdueLoans();
      setOverdueLoans(response.data);
      setError(null);
    } catch (err) {
      setError('연체 목록을 불러오는데 실패했습니다.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOverdueLoans();
  }, []);

  const handleReturn = async (loanId: number) => {
    if (!confirm('연체된 도서를 반납 처리하시겠습니까?')) return;
    
    try {
      await loanApi.returnBook(loanId);
      fetchOverdueLoans(); // 목록 새로고침
    } catch (err: any) {
      alert(err.response?.data?.message || '반납 처리에 실패했습니다.');
    }
  };

  const getOverdueDays = (dueDate: string) => {
    const today = new Date();
    const due = new Date(dueDate);
    return differenceInDays(today, due);
  };

  if (loading) {
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
        <div>
          <h2 className="text-2xl font-bold text-gray-900">연체 관리</h2>
          <p className="text-sm text-gray-600 mt-1">
            총 {overdueLoans.length}건의 연체된 대출이 있습니다.
          </p>
        </div>
        <button
          onClick={fetchOverdueLoans}
          className="bg-gray-600 text-white px-4 py-2 rounded-md hover:bg-gray-700"
        >
          🔄 새로고침
        </button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Empty State */}
      {overdueLoans.length === 0 && !loading && !error && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded text-center">
          🎉 현재 연체된 대출이 없습니다!
        </div>
      )}

      {/* Overdue Loans Table */}
      {overdueLoans.length > 0 && (
        <div className="bg-white shadow rounded-lg overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-red-50">
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
                  연체 기간
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  작업
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {overdueLoans.map((loan) => (
                <tr key={loan.id} className="hover:bg-red-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {loan.book.name}
                      </div>
                      <div className="text-sm text-gray-500">
                        저자: {loan.book.author}
                      </div>
                      <div className="text-sm text-gray-500">
                        ISBN: {loan.book.isbn}
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
                      <div className="text-sm text-gray-500">
                        {loan.user.phone}
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div>대출일: {format(new Date(loan.loanDate), 'yyyy-MM-dd')}</div>
                    <div className="text-red-600 font-medium">
                      반납예정: {format(new Date(loan.dueDate), 'yyyy-MM-dd')}
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex px-3 py-1 text-sm font-semibold rounded-full bg-red-100 text-red-800">
                      {getOverdueDays(loan.dueDate)}일 연체
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button
                      onClick={() => handleReturn(loan.id)}
                      className="text-green-600 hover:text-green-900 mr-3"
                    >
                      반납 처리
                    </button>
                    <a
                      href={`mailto:${loan.user.email}?subject=도서 반납 안내&body=안녕하세요 ${loan.user.name}님,%0A%0A대출하신 도서 "${loan.book.name}"이 반납 예정일을 ${getOverdueDays(loan.dueDate)}일 경과했습니다.%0A가능한 빨리 반납해주시기 바랍니다.%0A%0A감사합니다.`}
                      className="text-blue-600 hover:text-blue-900"
                    >
                      📧 알림 발송
                    </a>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Summary Statistics */}
      {overdueLoans.length > 0 && (
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-medium text-gray-900 mb-4">연체 현황 요약</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-red-50 p-4 rounded-lg">
              <div className="text-2xl font-bold text-red-600">
                {overdueLoans.length}
              </div>
              <div className="text-sm text-gray-600">총 연체 건수</div>
            </div>
            <div className="bg-orange-50 p-4 rounded-lg">
              <div className="text-2xl font-bold text-orange-600">
                {Math.max(...overdueLoans.map(loan => getOverdueDays(loan.dueDate)))}
              </div>
              <div className="text-sm text-gray-600">최대 연체 일수</div>
            </div>
            <div className="bg-yellow-50 p-4 rounded-lg">
              <div className="text-2xl font-bold text-yellow-600">
                {Math.round(
                  overdueLoans.reduce((sum, loan) => sum + getOverdueDays(loan.dueDate), 0) /
                  overdueLoans.length
                )}
              </div>
              <div className="text-sm text-gray-600">평균 연체 일수</div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OverdueList;
