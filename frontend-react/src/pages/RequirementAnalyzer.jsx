import { useState } from "react";
import {
  Alert,
  Box,
  Chip,
  CircularProgress,
  Divider,
  IconButton,
  InputAdornment,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import AutoAwesomeOutlinedIcon from "@mui/icons-material/AutoAwesomeOutlined";
import ChecklistRtlOutlinedIcon from "@mui/icons-material/ChecklistRtlOutlined";
import TuneOutlinedIcon from "@mui/icons-material/TuneOutlined";
import GroupsOutlinedIcon from "@mui/icons-material/GroupsOutlined";
import StorageOutlinedIcon from "@mui/icons-material/StorageOutlined";
import ApiOutlinedIcon from "@mui/icons-material/ApiOutlined";
import ReportGmailerrorredOutlinedIcon from "@mui/icons-material/ReportGmailerrorredOutlined";
import GavelOutlinedIcon from "@mui/icons-material/GavelOutlined";
import { analyzeRequirements } from "../api/requirement";

const PRIORITY_COLORS = {
  HIGH: { bg: "#fde8ef", color: "#db2777" },
  MEDIUM: { bg: "#ffeede", color: "#ea580c" },
  LOW: { bg: "#e6f7ee", color: "#16a34a" },
};

const METHOD_COLORS = {
  GET: { bg: "#e6f0ff", color: "#2563eb" },
  POST: { bg: "#e6f7ee", color: "#16a34a" },
  PUT: { bg: "#ffeede", color: "#ea580c" },
  PATCH: { bg: "#efe7ff", color: "#7c3aed" },
  DELETE: { bg: "#fde8ef", color: "#db2777" },
};

const ACTOR_COLORS = {
  PRIMARY: { bg: "#efe7ff", color: "#7c3aed" },
  SECONDARY: { bg: "#e6f0ff", color: "#2563eb" },
  SYSTEM: { bg: "#f3f4f6", color: "#4b5563" },
};

const SectionCard = ({ icon, title, count, children }) => (
  <Paper
    elevation={0}
    sx={{ p: { xs: 2.5, md: 3 }, borderRadius: 3, border: "1px solid #ececf1" }}
  >
    <Stack direction="row" spacing={1.25} alignItems="center" mb={2}>
      <Box
        sx={{
          width: 36,
          height: 36,
          borderRadius: 2,
          bgcolor: "#efe7ff",
          color: "#7c3aed",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        {icon}
      </Box>
      <Typography variant="h6" fontWeight={800}>
        {title}
      </Typography>
      {count != null && (
        <Chip
          label={count}
          size="small"
          sx={{ bgcolor: "#f3f4f6", fontWeight: 700, height: 22 }}
        />
      )}
    </Stack>
    {children}
  </Paper>
);

const Tag = ({ label, colors }) => (
  <Chip
    label={label}
    size="small"
    sx={{
      bgcolor: colors?.bg || "#f3f4f6",
      color: colors?.color || "#4b5563",
      fontWeight: 700,
      fontSize: 11,
      height: 22,
    }}
  />
);

const RequirementAnalyzer = () => {
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [analysis, setAnalysis] = useState(null);

  const handleAnalyze = async (event) => {
    event?.preventDefault();
    const q = query.trim();
    if (!q || loading) return;

    setError("");
    setLoading(true);
    try {
      const { data } = await analyzeRequirements(q);
      setAnalysis(data);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.message ||
          "Failed to analyze requirements. Please try again."
      );
      setAnalysis(null);
    } finally {
      setLoading(false);
    }
  };

  const {
    projectName,
    summary,
    functionalRequirements = [],
    nonFunctionalRequirements = [],
    actors = [],
    entities = [],
    apiEndpoints = [],
    edgeCases = [],
    businessRules = [],
  } = analysis || {};

  return (
    <Box>
      <Typography variant="h4" fontWeight={800} mb={3}>
        Requirement Analyzer
      </Typography>

      <Stack spacing={3}>
        <Paper
          elevation={0}
          sx={{ p: { xs: 2.5, md: 3 }, borderRadius: 3, border: "1px solid #ececf1" }}
        >
          <Typography variant="body2" color="text.secondary" mb={2}>
            Describe a feature or product and get a structured requirements breakdown.
          </Typography>

          <Box component="form" onSubmit={handleAnalyze}>
            <TextField
              fullWidth
              placeholder="e.g. Tic Tac Toe"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              size="small"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchRoundedIcon sx={{ color: "#9ca3af" }} />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      type="submit"
                      disabled={loading || !query.trim()}
                      sx={{
                        width: 36,
                        height: 36,
                        color: "#fff",
                        background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
                        "&:hover": {
                          background: "linear-gradient(135deg, #6d28d9, #4c1d95)",
                        },
                        "&.Mui-disabled": { background: "#e5e7eb", color: "#9ca3af" },
                      }}
                    >
                      {loading ? (
                        <CircularProgress size={16} sx={{ color: "#9ca3af" }} />
                      ) : (
                        <AutoAwesomeOutlinedIcon fontSize="small" />
                      )}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ "& .MuiOutlinedInput-root": { borderRadius: 2.5, pr: 0.75 } }}
            />
          </Box>
        </Paper>

        {error && <Alert severity="error">{error}</Alert>}

        {loading && !analysis && (
          <Stack alignItems="center" py={6}>
            <CircularProgress sx={{ color: "#7c3aed" }} />
          </Stack>
        )}

        {!loading && !analysis && !error && (
          <Stack alignItems="center" textAlign="center" spacing={1.5} py={6}>
            <Box
              sx={{
                width: 64,
                height: 64,
                borderRadius: "50%",
                bgcolor: "#efe7ff",
                color: "#7c3aed",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <ChecklistRtlOutlinedIcon sx={{ fontSize: 30 }} />
            </Box>
            <Typography fontWeight={700}>No analysis yet</Typography>
            <Typography variant="body2" color="text.secondary">
              Enter a feature or product name above to generate requirements.
            </Typography>
          </Stack>
        )}

        {analysis && (
          <>
            {/* Summary */}
            <Paper
              elevation={0}
              sx={{
                p: { xs: 2.5, md: 3 },
                borderRadius: 3,
                border: "1px solid #ececf1",
                background: "linear-gradient(135deg, #f5f3ff, #faf5ff)",
              }}
            >
              <Typography variant="h5" fontWeight={800} mb={0.5}>
                {projectName}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                {summary}
              </Typography>
            </Paper>

            {/* Functional Requirements */}
            {functionalRequirements.length > 0 && (
              <SectionCard
                icon={<ChecklistRtlOutlinedIcon fontSize="small" />}
                title="Functional Requirements"
                count={functionalRequirements.length}
              >
                <Stack spacing={1.5}>
                  {functionalRequirements.map((fr) => (
                    <Box
                      key={fr.id}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Stack
                        direction="row"
                        justifyContent="space-between"
                        alignItems="center"
                        spacing={1}
                        mb={0.5}
                      >
                        <Stack direction="row" spacing={1} alignItems="center">
                          <Typography
                            variant="caption"
                            sx={{ color: "#7c3aed", fontWeight: 800 }}
                          >
                            {fr.id}
                          </Typography>
                          <Typography fontWeight={700} fontSize={14}>
                            {fr.title}
                          </Typography>
                        </Stack>
                        <Tag label={fr.priority} colors={PRIORITY_COLORS[fr.priority]} />
                      </Stack>
                      <Typography variant="body2" color="text.secondary">
                        {fr.description}
                      </Typography>
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}

            {/* Non-Functional Requirements */}
            {nonFunctionalRequirements.length > 0 && (
              <SectionCard
                icon={<TuneOutlinedIcon fontSize="small" />}
                title="Non-Functional Requirements"
                count={nonFunctionalRequirements.length}
              >
                <Stack spacing={1.5}>
                  {nonFunctionalRequirements.map((nfr) => (
                    <Box
                      key={nfr.id}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Stack direction="row" spacing={1} alignItems="center" mb={0.5}>
                        <Typography
                          variant="caption"
                          sx={{ color: "#7c3aed", fontWeight: 800 }}
                        >
                          {nfr.id}
                        </Typography>
                        <Tag label={nfr.category} />
                      </Stack>
                      <Typography variant="body2" color="text.secondary">
                        {nfr.description}
                      </Typography>
                      {nfr.metric && (
                        <Typography
                          variant="caption"
                          sx={{ color: "#6b7280", mt: 0.75, display: "block" }}
                        >
                          📏 {nfr.metric}
                        </Typography>
                      )}
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}

            {/* Actors */}
            {actors.length > 0 && (
              <SectionCard
                icon={<GroupsOutlinedIcon fontSize="small" />}
                title="Actors"
                count={actors.length}
              >
                <Stack spacing={1.5}>
                  {actors.map((actor) => (
                    <Box
                      key={actor.name}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Stack direction="row" spacing={1} alignItems="center" mb={0.75}>
                        <Typography fontWeight={700} fontSize={14}>
                          {actor.name}
                        </Typography>
                        <Tag label={actor.type} colors={ACTOR_COLORS[actor.type]} />
                      </Stack>
                      <Stack direction="row" spacing={0.75} flexWrap="wrap" useFlexGap>
                        {(actor.responsibilities || []).map((r) => (
                          <Chip
                            key={r}
                            label={r}
                            size="small"
                            variant="outlined"
                            sx={{ fontSize: 11, height: 22, borderColor: "#ececf1" }}
                          />
                        ))}
                      </Stack>
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}

            {/* Entities */}
            {entities.length > 0 && (
              <SectionCard
                icon={<StorageOutlinedIcon fontSize="small" />}
                title="Entities"
                count={entities.length}
              >
                <Stack spacing={1.5}>
                  {entities.map((entity) => (
                    <Box
                      key={entity.name}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Typography fontWeight={700} fontSize={14} mb={0.75}>
                        {entity.name}
                      </Typography>
                      <Typography variant="caption" color="text.secondary" fontWeight={700}>
                        Attributes
                      </Typography>
                      <Stack
                        direction="row"
                        spacing={0.75}
                        flexWrap="wrap"
                        useFlexGap
                        mt={0.5}
                        mb={1}
                      >
                        {(entity.attributes || []).map((a) => (
                          <Tag key={a} label={a} colors={{ bg: "#f5f3ff", color: "#6d28d9" }} />
                        ))}
                      </Stack>
                      {(entity.relationships || []).length > 0 && (
                        <>
                          <Typography
                            variant="caption"
                            color="text.secondary"
                            fontWeight={700}
                          >
                            Relationships
                          </Typography>
                          <Stack
                            direction="row"
                            spacing={0.75}
                            flexWrap="wrap"
                            useFlexGap
                            mt={0.5}
                          >
                            {entity.relationships.map((r) => (
                              <Chip
                                key={r}
                                label={r}
                                size="small"
                                variant="outlined"
                                sx={{ fontSize: 11, height: 22, borderColor: "#ececf1" }}
                              />
                            ))}
                          </Stack>
                        </>
                      )}
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}

            {/* API Endpoints */}
            {apiEndpoints.length > 0 && (
              <SectionCard
                icon={<ApiOutlinedIcon fontSize="small" />}
                title="API Endpoints"
                count={apiEndpoints.length}
              >
                <Stack spacing={1.5}>
                  {apiEndpoints.map((ep, i) => (
                    <Box
                      key={`${ep.method}-${ep.path}-${i}`}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Stack direction="row" spacing={1} alignItems="center" mb={0.5}>
                        <Tag label={ep.method} colors={METHOD_COLORS[ep.method]} />
                        <Typography
                          fontFamily="monospace"
                          fontSize={13}
                          fontWeight={600}
                          sx={{ wordBreak: "break-all" }}
                        >
                          {ep.path}
                        </Typography>
                      </Stack>
                      <Typography variant="body2" color="text.secondary" mb={0.75}>
                        {ep.description}
                      </Typography>
                      <Stack
                        direction={{ xs: "column", sm: "row" }}
                        divider={<Divider orientation="vertical" flexItem />}
                        spacing={{ xs: 0.5, sm: 2 }}
                      >
                        <Typography variant="caption" color="text.secondary">
                          <b>Request:</b> {ep.requestSummary}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          <b>Response:</b> {ep.responseSummary}
                        </Typography>
                      </Stack>
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}

            {/* Edge Cases */}
            {edgeCases.length > 0 && (
              <SectionCard
                icon={<ReportGmailerrorredOutlinedIcon fontSize="small" />}
                title="Edge Cases"
                count={edgeCases.length}
              >
                <Stack spacing={1.5}>
                  {edgeCases.map((ec, i) => (
                    <Box
                      key={i}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Typography fontWeight={700} fontSize={14} mb={0.5}>
                        {ec.scenario}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {ec.expectedHandling}
                      </Typography>
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}

            {/* Business Rules */}
            {businessRules.length > 0 && (
              <SectionCard
                icon={<GavelOutlinedIcon fontSize="small" />}
                title="Business Rules"
                count={businessRules.length}
              >
                <Stack spacing={1.5}>
                  {businessRules.map((br) => (
                    <Box
                      key={br.id}
                      sx={{ border: "1px solid #ececf1", borderRadius: 2, p: 2 }}
                    >
                      <Stack direction="row" spacing={1} alignItems="center" mb={0.5}>
                        <Typography
                          variant="caption"
                          sx={{ color: "#7c3aed", fontWeight: 800 }}
                        >
                          {br.id}
                        </Typography>
                        <Typography fontWeight={700} fontSize={14}>
                          {br.rule}
                        </Typography>
                      </Stack>
                      {br.rationale && (
                        <Typography variant="body2" color="text.secondary">
                          {br.rationale}
                        </Typography>
                      )}
                    </Box>
                  ))}
                </Stack>
              </SectionCard>
            )}
          </>
        )}
      </Stack>
    </Box>
  );
};

export default RequirementAnalyzer;
