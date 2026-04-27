import React, { useState, useRef } from 'react';
import './App.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

function CriterionCard({ item }) {
  return (
    <div className={`criterion-card level-${item.level}`}>
      <div className="criterion-header">
        <span className="criterion-name">{item.criterion}</span>
        <span className={`level-badge ${item.level}`}>{item.level}</span>
      </div>
      <p className="criterion-feedback">{item.feedback}</p>
    </div>
  );
}

function ListSection({ title, items }) {
  return (
    <div className="list-section">
      <h3>{title}</h3>
      <ul>
        {items.map((item, i) => (
          <li key={i}>{item}</li>
        ))}
      </ul>
    </div>
  );
}

function App() {
  const [text, setText] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fileName, setFileName] = useState('');
  const fileInputRef = useRef(null);

  const handleResult = (data, ok, errorText) => {
    if (!ok) throw new Error(errorText || 'Ukendt fejl fra serveren');
    setResult(data);
  };

  const evaluate = async () => {
    if (!text.trim()) {
      setError('Indtast en opgavetekst før evaluering.');
      return;
    }
    setLoading(true);
    setError('');
    setResult(null);
    try {
      const response = await fetch(`${API_URL}/api/evaluations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text }),
      });
      const data = await response.json();
      handleResult(data, response.ok, data.error);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleFile = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const name = file.name.toLowerCase();
    if (!name.endsWith('.pdf') && !name.endsWith('.md')) {
      setError('Kun .pdf og .md filer er understøttet.');
      e.target.value = '';
      return;
    }

    setFileName(file.name);
    setLoading(true);
    setError('');
    setResult(null);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch(`${API_URL}/api/evaluations/file`, {
        method: 'POST',
        body: formData,
      });
      const data = await response.json();
      handleResult(data, response.ok, data.error);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
      e.target.value = '';
    }
  };

  return (
    <div className="app">
      <header>
        <h1>AI Praktikrapport-vurdering</h1>
        <p>Indsæt en praktikrapport og få en vejledende AI-baseret vurdering.</p>
      </header>

      <div className="input-card">
        <div className="upload-zone" onClick={() => fileInputRef.current.click()}>
          <span className="upload-icon">&#8679;</span>
          <span className="upload-label">
            {fileName ? fileName : 'Upload fil (.pdf eller .md)'}
          </span>
          <span className="upload-hint">Klik for at vælge fil</span>
        </div>
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf,.md"
          onChange={handleFile}
          style={{ display: 'none' }}
        />

        <div className="divider"><span>eller</span></div>

        <label htmlFor="report-text">Indsæt tekst manuelt</label>
        <textarea
          id="report-text"
          rows={14}
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Indsæt rapportteksten her..."
          disabled={loading}
        />
        <button onClick={evaluate} disabled={loading}>
          {loading ? 'Vurderer...' : 'Evaluér'}
        </button>
        {error && <p className="error-msg">{error}</p>}
      </div>

      {result && (
        <div className="result-card">
          <h2>Vejledende vurdering</h2>

          <div className="overall-assessment">
            {result.overallAssessment}
          </div>

          <p className="section-title">Kriterier</p>
          <div className="criteria-list">
            {result.criteriaFeedback.map((item, i) => (
              <CriterionCard key={i} item={item} />
            ))}
          </div>

          <div className="lists-grid">
            <ListSection title="Styrker" items={result.strengths} />
            <ListSection title="Svagheder" items={result.weaknesses} />
            <ListSection title="Forbedringsforslag" items={result.improvements} />
            <ListSection title="Spørgsmål til dialog" items={result.questions} />
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
