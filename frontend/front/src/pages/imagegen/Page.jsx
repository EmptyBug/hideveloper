import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function TalkGenPage() {
  const navigate = useNavigate();
  
  const [images, setImages] = useState([]);
  const [generation, setGeneration] = useState(1);

  // 1. 서버에서 이미지 세대를 불러오는 함수 (기존과 동일)
  const fetchImages = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/image/noise');  
      const data = await response.json();
      
      if (Array.isArray(data.images)) {
        setImages(data.images);
        setGeneration(data.generation);
      } else {
        console.error("오류: 데이터 형식이 맞지 않습니다.");
      }
    } catch (error) {
      console.error("백엔드와 연결할 수 없습니다.", error);
    }
  };

  // 2. ★ 새로 추가된 투표 전송 함수 ★
  const handleVote = async (selectedIndex) => {
    try {
      // 백엔드의 DTO(VoteRequest) 형태에 맞춰 보낼 데이터를 만듭니다.
      const voteData = {
        generation: generation,
        selectedIndex: selectedIndex // 1, 2, 3 중 하나
      };

      // POST 방식으로 /api/vote 주소에 데이터를 쏩니다.
      const response = await fetch('http://localhost:8080/api/vote', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(voteData),
      });

      if (response.ok) {
        // 백엔드에서 정상적으로 처리되었다면 알림을 띄웁니다.
        alert(`${generation}세대의 ${selectedIndex}번 이미지에 투표 완료!`);
        
        // 투표 후에 새로운 이미지를 받고 싶다면 여기서 fetchImages()를 다시 호출해도 좋습니다.
        // fetchImages(); 
      } else {
        alert("투표 처리에 실패했습니다.");
      }
    } catch (error) {
      console.error("투표 전송 중 오류 발생:", error);
    }
  };

  useEffect(() => {
    // 1. 화면에 들어오자마자 최초 1회 즉시 실행
    fetchImages();

    // 2. 3초(3000ms)마다 fetchImages 함수를 무한 반복 실행 (새로운 세대가 나왔는지 계속 감시!)
    const pollingInterval = setInterval(() => {
      fetchImages();
    }, 3000);

    // 3. 유저가 다른 페이지로 이동하면 감시(타이머)를 종료해서 메모리 누수 방지
    return () => clearInterval(pollingInterval);
  }, []);

  return (
    <div style={styles.wrapper}>
      <div style={styles.container}>
        <h2>🧬 유전 알고리즘 (현재: {generation}세대)</h2>
        <p>가장 마음에 드는 이미지를 하나 선택(투표)해 주세요!</p>
        
        <div style={styles.imageGrid}>
          {Array.isArray(images) && images.length > 0 ? (
            images.map((imgUrl, index) => (
              <div key={index} style={styles.imageCard}>
                <img 
                  src={imgUrl} 
                  alt={`${generation}세대 이미지 ${index + 1}`} 
                  style={styles.noiseImage} 
                />
                {/* 3. ★ 버튼 클릭 시 handleVote 함수를 실행하도록 연결 ★ */}
                <button 
                  style={styles.voteButton}
                  onClick={() => handleVote(index + 1)}
                >
                  {index + 1}번 선택
                </button>
              </div>
            ))
          ) : (
            <p>서버에서 세대 이미지를 불러오는 중...</p>
          )}
        </div>
        
        <button onClick={() => navigate('/')} style={styles.backButton}>
          메인으로 돌아가기
        </button>
      </div>
    </div>
  );
}

const styles = {
  wrapper: {
    display: 'flex', justifyContent: 'center', alignItems: 'center',
    width: '100vw', height: '100vh', backgroundColor: '#f8f9fa',
    margin: 0, padding: 0, boxSizing: 'border-box'
  },
  container: {
    display: 'flex', flexDirection: 'column', alignItems: 'center',
    textAlign: 'center', fontFamily: 'sans-serif',
  },
  imageGrid: {
    display: 'flex', gap: '30px', margin: '30px 0'
  },
  imageCard: {
    display: 'flex', flexDirection: 'column', alignItems: 'center',
    backgroundColor: '#fff', padding: '15px', borderRadius: '15px',
    boxShadow: '0 4px 8px rgba(0,0,0,0.1)'
  },
  noiseImage: {
    width: '200px', height: '200px', objectFit: 'cover', 
    borderRadius: '8px', marginBottom: '15px'
  },
  voteButton: {
    padding: '10px 20px', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
    backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '8px',
    width: '100%', transition: '0.2s'
  },
  backButton: {
    padding: '10px 20px', fontSize: '15px', cursor: 'pointer',
    backgroundColor: '#333', color: '#fff', border: 'none',
    borderRadius: '5px', marginTop: '20px'
  }
};

export default TalkGenPage;