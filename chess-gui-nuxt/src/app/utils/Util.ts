export async function sha256(password: string): Promise<string> {
	const encoder = new TextEncoder()
	const data = encoder.encode(password)
	const hashBuffer = await crypto.subtle.digest("SHA-256", data)
	const hashArray = Array.from(new Uint8Array(hashBuffer))
	const hashHex = hashArray.map(b => b.toString(16).padStart(2, "0")).join("")
	return hashHex
}

export function isBlank(str: string) {
	return !str || /^\s*$/.test(str);
}
