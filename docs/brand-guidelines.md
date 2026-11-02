# Brand Guidelines v1.0 — رقيم (Raqeem)

## Quick Reference
- Primary Color | #10B981
- Secondary Color | #059669
- Accent Color | #F43F5E
- Primary Font: Cairo, IBM Plex Sans Arabic, sans-serif
- Voice: Intelligent, Empathetic, Financial-Grade, Arabic-First

---

## 1. Color Palette

### Primary Colors
| Name | Hex | RGB | Usage |
|------|-----|-----|-------|
| Emerald Primary | #10B981 | rgb(16, 185, 129) | Primary brand color, Voice CTA, active pulses, positive savings |
| Emerald Dark | #059669 | rgb(5, 150, 105) | Gradients, confirmed states, container borders |
| Emerald Light | #34D399 | rgb(52, 211, 153) | Glow effects, waveform peaks, highlights |

### Secondary Colors
| Name | Hex | RGB | Usage |
|------|-----|-----|-------|
| Blue Payer | #3B82F6 | rgb(59, 130, 246) | Payer badges ("محمد", "أحمد"), shared expense splits |
| Blue Dark | #1D4ED8 | rgb(29, 78, 216) | Secondary cards, bank SMS verification states |
| Blue Light | #93C5FD | rgb(147, 197, 253) | Secondary text accents, payer chips background |

### Accent Colors
| Name | Hex | RGB | Usage |
|------|-----|-----|-------|
| Coral Expense | #F43F5E | rgb(244, 63, 94) | Expense amounts, live recording pulse, alerts |
| Coral Dark | #BE123C | rgb(190, 18, 60) | Danger buttons, budget threshold warnings |
| Coral Light | #FDA4AF | rgb(253, 164, 175) | Expense badge backgrounds |

### Neutral Palette
| Name | Hex | RGB | Usage |
|------|-----|-----|-------|
| Background Dark | #0F172A | rgb(15, 23, 42) | Deep Obsidian Slate background |
| Surface Dark | #1E293B | rgb(30, 41, 59) | Container cards, bottom navigation, modals |
| Surface Card | #334155 | rgb(51, 65, 85) | Expense item rows, dialog surfaces, inputs |
| Text Primary | #F8FAFC | rgb(248, 250, 252) | Primary typography, headers, currency numbers |
| Text Secondary | #94A3B8 | rgb(148, 163, 184) | Descriptions, dates, categories, hint text |
| Border Slate | #475569 | rgb(71, 85, 105) | Dividers, card borders, subtle separators |

### Accessibility
- Text/Background Contrast: 14.8:1 (WCAG AAA compliant)
- CTA Contrast: 7.2:1 (WCAG AAA compliant)
- All touch targets strictly follow >= 48dp / 56dp mobile standards
- Voice recording button: 72dp with dynamic ambient pulse

---

## 2. Typography

### Font Stack
```css
--font-heading: 'Cairo', 'IBM Plex Sans Arabic', -apple-system, sans-serif;
--font-body: 'Cairo', 'IBM Plex Sans Arabic', -apple-system, sans-serif;
--font-mono: 'JetBrains Mono', 'Fira Code', monospace;
```

### Type Scale
| Element | Font | Weight | Size (Compose / Web) | Line Height |
|---------|------|--------|----------------------|-------------|
| Display Large | Cairo | 800 (ExtraBold) | 32sp / 36px | 1.2 |
| Title Large | Cairo | 700 (Bold) | 24sp / 28px | 1.25 |
| Title Medium | Cairo | 600 (SemiBold) | 18sp / 22px | 1.3 |
| Body Large | Cairo | 500 (Medium) | 15sp / 18px | 1.4 |
| Body Medium | Cairo | 400 (Regular) | 13sp / 16px | 1.4 |
| Label Small | Cairo | 700 (Bold) | 11sp / 13px | 1.2 |

---

## 3. Logo Usage & Identity

### Variants
- **Primary Mark:** Squircle badge with geometric Arabic letter 'R' (ر) intertwined with audio waveform & dynamic ledger balance dot.
- **Wordmark:** رقيم (Raqeem) — Modern Arabic geometric lettering with custom financial ligature.
- **Lockup:** Horizontal combo for banners and store headers; centered squircle for Android adaptive icons.

### Clear Space
- Minimum clear space equals 25% of the symbol width on all four sides.
- Minimum digital resolution: 48x48dp; vector SVG preferred.

---

## 4. Voice & Tone (Arabic-First)

### Brand Personality
- **ذكي وهادئ (Intelligent & Calm):** يتعامل مع المال بوضوح بدون تعقيد أو تشويش.
- **عفوي وسريع (Spontaneous & Swift):** يسجل الصوت باللهجات الخليجية والمصرية والشامية والمغاربية فوراً دون الحاجة لكتابة أو تصنيف يدوي.
- **أمين وموثوق (Secure & Trusted):** معالجة محلية Offline-First للبيانات مع خصوصية تامة وحذف التسجيلات الصوتية المؤقتة.

### Voice Rules for Screens
- استخدم مصطلحات مالية شعبية واضحة: "مين دفع؟", "لوحة المصاريف", "رادار الأسبوع", "وفرت 450 ر.س".
- الأرقام والعملات تتبع التنسيق العربي الأصيل من اليمين لليسار.

