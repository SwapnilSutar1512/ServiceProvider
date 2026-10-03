export const getApiErrorMessage = (error, fallback = 'Something went wrong. Please try again.') => {
  return error?.response?.data?.message || error?.message || fallback
}

export const getFieldErrorsFromMessage = (message, fieldRules = {}) => {
  const normalized = String(message || '').toLowerCase()
  const errors = {}

  Object.entries(fieldRules).forEach(([field, rules]) => {
    const match = rules.some((rule) => normalized.includes(String(rule).toLowerCase()))
    if (match) {
      errors[field] = rules[0]
    }
  })

  return errors
}

export const getFormFieldError = (message, field, fallbackMessage) => {
  if (!message) return ''
  const normalized = String(message).toLowerCase()
  if (normalized.includes(field.toLowerCase())) return fallbackMessage
  return ''
}
