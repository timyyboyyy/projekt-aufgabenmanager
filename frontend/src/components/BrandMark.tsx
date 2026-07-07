/** Logo-Zeichen: abgerundetes Quadrat mit Haken (Verlauf), fuer Nav und Login. */
export default function BrandMark({ size = 28 }: { size?: number }) {
  return (
    <svg
      className="brand-mark"
      width={size}
      height={size}
      viewBox="0 0 32 32"
      role="img"
      aria-label="Aufgabenmanager"
    >
      <defs>
        <linearGradient id="brandmark-grad" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stopColor="#6366f1" />
          <stop offset="1" stopColor="#8b5cf6" />
        </linearGradient>
      </defs>
      <rect width="32" height="32" rx="9" fill="url(#brandmark-grad)" />
      <path
        d="M9 16.5l4.5 4.5L23 11"
        fill="none"
        stroke="#fff"
        strokeWidth="2.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}
