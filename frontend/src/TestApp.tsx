function TestApp() {
  return (
    <div style={{ padding: '20px' }}>
      <h1>테스트 페이지</h1>
      <p>이 메시지가 보이면 React가 정상 작동합니다.</p>
      <button onClick={() => alert('버튼 클릭됨!')}>
        클릭 테스트
      </button>
    </div>
  );
}

export default TestApp;
