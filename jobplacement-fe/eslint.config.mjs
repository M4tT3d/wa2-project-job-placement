// @ts-check
import pluginJs from "@eslint/js"
import pluginQuery from "@tanstack/eslint-plugin-query"
import pluginRouter from "@tanstack/eslint-plugin-router"
import pluginPrettier from "eslint-plugin-prettier/recommended"
import pluginReact from "eslint-plugin-react"
import reactRefresh from "eslint-plugin-react-refresh"
import pluginTailwind from "eslint-plugin-tailwindcss"
import globals from "globals"
import tseslint from "typescript-eslint"

export default tseslint.config(
  { ignores: ["dist/"] },
  pluginJs.configs.recommended,
  ...tseslint.configs.recommended,
  pluginReact.configs.flat["jsx-runtime"],
  ...pluginTailwind.configs["flat/recommended"],
  pluginPrettier,
  ...pluginQuery.configs["flat/recommended"],
  ...pluginRouter["flat/recommended"],
  {
    plugins: {
      "react-refresh": reactRefresh,
    },
    languageOptions: {
      globals: globals.browser,
    },
    rules: {
      "react-refresh/only-export-components": ["warn", { allowConstantExport: true }],
      "@typescript-eslint/no-unused-vars": "warn",
    },
  },
)
