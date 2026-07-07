/** Fortschrittsbalken (US6): fuellt sich entsprechend dem Prozentwert. */
export default function ProgressBar({ percent }: { percent: number }) {
  return (
    <div className="progress-row">
      <div
        className="progress"
        role="progressbar"
        aria-valuenow={percent}
        aria-valuemin={0}
        aria-valuemax={100}
      >
        <div className="progress-fill" style={{ width: `${percent}%` }} />
      </div>
      <span className="progress-value">{percent}%</span>
    </div>
  )
}
