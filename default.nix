{ pkgs ? import <nixpkgs> { } }:

pkgs.stdenv.mkDerivation {
  name = "clojure-art";
  src = pkgs.lib.cleanSource ./.;
  preferLocalBuild = true;
  buildInputs = with pkgs; [ leiningen clojure nodejs clojure-lsp ];
}
